package nh.recipify.domain.api.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import nh.recipify.WebConfigProps;
import nh.recipify.domain.api.CategoryDto;
import nh.recipify.domain.api.DetailedRecipeDto;
import nh.recipify.domain.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/admin")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://localhost:8090", "http://localhost:8091", "http://localhost:8100", "https://*:8100"})
@Tag(name = "Admin", description = "Admin Endpoints")
@Validated
class AdminController {

    private static final Logger log = LoggerFactory.getLogger( AdminController.class );

    private final WebConfigProps webConfigProps;
    private final MealtypeRepository mealtypeRepository;
    private final CategoryRepository categoryRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    AdminController(WebProperties webProperties, WebConfigProps webConfigProps, MealtypeRepository mealtypeRepository, CategoryRepository categoryRepository, RecipeRepository recipeRepository, UserRepository userRepository) {
        this.webConfigProps = webConfigProps;
        this.mealtypeRepository = mealtypeRepository;
        this.categoryRepository = categoryRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    record MeDto(@NotNull String fullname) {}

    @GetMapping("me")
    public ResponseEntity<MeDto> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return ResponseEntity.ok(new MeDto(
            user.getFullname())
        );
    }

    @GetMapping("meal-types")
    List<MealTypeDto> getMealTypes() {
        return mealtypeRepository.findAllByOrderByName()
            .stream().map(MealTypeDto::of).toList();
    }

    @GetMapping("categories")
    List<CategoryDto> getCategories() {

        return categoryRepository.findAllByOrderByTitle()
            .stream().map(CategoryDto::of).toList();
    }

    record CreateRecipeRequest(
        @NotNull String title,
        @NotNull String headline,
        int preparationTime,
        int cookTime,
        @NotNull String mealTypeId,
        @NotNull List<String> categoryIds,
        @NotNull List<String> instructions,
        @NotNull List<IngredientRequest> ingredients,
        @NotNull String image
    ) {

        record IngredientRequest(
            @NotNull double amount,
            @NotNull String unit,
            @NotNull String name
        ) {}

    }

    @PostMapping("/recipe")
    @Valid
    ResponseEntity<@Valid DetailedRecipeDto> createRecipe(@NotNull @Valid @RequestBody CreateRecipeRequest request) throws IOException {

        var mealType = mealtypeRepository.findById(Long.parseLong(request.mealTypeId()))
            .orElseThrow();

        var categories = request.categoryIds().stream()
            .map(Long::parseLong)
            .map(id -> categoryRepository.findById(id).orElseThrow())
            .collect(Collectors.toSet());

        var user = userRepository.findById(1L).orElseThrow();

        String base64Data = request.image.split(",")[1];
        byte[] fileBytes = Base64.getDecoder().decode(base64Data);

        Path uploadDirPath = webConfigProps.uploadDir().startsWith("file:")
            ? Paths.get(URI.create(webConfigProps.uploadDir()))
            : Paths.get(webConfigProps.uploadDir());

        Files.createDirectories(uploadDirPath);

        var filename = "image_" + UUID.randomUUID()+".png";


        var uploadFileTarget = uploadDirPath.resolve(filename);
        log.info("Save file to '{}'", uploadFileTarget);
        Files.write(uploadFileTarget, fileBytes);

        var newRecipe = new Recipe(
            user,
            request.title(),
            request.headline(),
            request.preparationTime(),
            request.cookTime(),
            mealType,
            categories,
            webConfigProps.uploadUrlPath() + filename
        );

        for (int i=0;i<request.instructions().size();i++) {
            newRecipe.addInstruction(i+1, request.instructions().get(i));
        }

        for (int i=0;i<request.ingredients().size();i++) {
            var ingredient = request.ingredients().get(i);
            newRecipe.addIngredient(
                i+1,
                ingredient.amount(),
                ingredient.unit(),
                ingredient.name()
            );
        }

        newRecipe = recipeRepository.save(newRecipe);

        log.info("new recipe {}", newRecipe);

        var recipeDto = DetailedRecipeDto.of(newRecipe);

        return ResponseEntity.ok(recipeDto);

    }

}
