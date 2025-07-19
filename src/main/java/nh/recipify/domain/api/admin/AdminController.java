package nh.recipify.domain.api.admin;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import nh.recipify.BadRequestException;
import nh.recipify.WebConfigProps;
import nh.recipify.domain.api.CategoryDto;
import nh.recipify.domain.api.ImageDto;
import nh.recipify.domain.api.PageResponse;
import nh.recipify.domain.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static nh.recipify.domain.api.Utils.sleepFor;

@RestController
@RequestMapping(path = "/api/admin")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://localhost:8090", "http://localhost:8091", "http://localhost:8100", "https://*:8100"})
@Tag(name = "Admin", description = "Admin Endpoints")
@Validated
class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final WebConfigProps webConfigProps;
    private final MealtypeRepository mealtypeRepository;
    private final CategoryRepository categoryRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final AdminRecipeRepository adminRecipeRepository;
    private final FeedbackRepository feedbackRepository;

    AdminController(WebProperties webProperties, WebConfigProps webConfigProps, MealtypeRepository mealtypeRepository, CategoryRepository categoryRepository, RecipeRepository recipeRepository, UserRepository userRepository, ImageRepository imageRepository, AdminRecipeRepository adminRecipeRepository, FeedbackRepository feedbackRepository) {
        this.webConfigProps = webConfigProps;
        this.mealtypeRepository = mealtypeRepository;
        this.categoryRepository = categoryRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.adminRecipeRepository = adminRecipeRepository;
        this.feedbackRepository = feedbackRepository;
    }

    record MeDto(@NotNull String fullname) {
    }

    @GetMapping("me")
    public ResponseEntity<MeDto> getMe(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Optional<Long> slowdown) {

        sleepFor("API GET /me", slowdown);

        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return ResponseEntity.ok(new MeDto(
            user.getFullname())
        );
    }

    @GetMapping("/recipe-dashboard-list")
    PageResponse<AdminRecipeDto> getRecipeDashboardList(@RequestParam Optional<Integer> page,
                                                   @RequestParam Optional<Integer> size,
                                                   @RequestParam("slowdown") Optional<Long> slowDown) {

        if (size.isPresent() && size.get() < 1) {
            throw new BadRequestException("Invalid argument 'size': '%s'. Must be greater than 0".formatted(size.get()));
        }

        if (page.isPresent() && page.get() < 0) {
            throw new BadRequestException("Invalid argument 'page': '%s'. Must be at least 0".formatted(page.get()));
        }

        sleepFor("API /admin/recipe-dashboard-list", slowDown);

        var pageable = PageRequest.of(page.orElse(0), size.orElse(5));

        var pagedResult = adminRecipeRepository.findRecipesWithFeedbackCount(pageable)
            .map(AdminRecipeDto::of);

        return PageResponse.of(pagedResult);
    }

    @GetMapping("images")
    List<ImageDto> getAllImages(@RequestParam Optional<Long> slowdown) {
        sleepFor("API GET /images", slowdown);
        return imageRepository.findAllByOrderByCreatedAtDescTitle()
            .stream().map(ImageDto::of).toList();
    }

    @GetMapping("images/{imageId}")
    ImageDto getImageById(@PathVariable String imageId, @RequestParam Optional<Long> slowdown) {
        sleepFor("API GET /images/" + imageId, slowdown);
        return imageRepository.findById(Long.parseLong(imageId))
            .map(ImageDto::of).orElseThrow(() -> new EntityNotFoundException("Image with id '%s' not found".formatted(imageId)));

    }

    @GetMapping("meal-types")
    List<MealTypeDto> getMealTypes(@RequestParam Optional<Long> slowdown) {
        sleepFor("API GET /meal-types", slowdown);
        return mealtypeRepository.findAllByOrderByName()
            .stream().map(MealTypeDto::of).toList();
    }

    @GetMapping("categories")
    List<CategoryDto> getCategories(@RequestParam Optional<Long> slowdown) {
        sleepFor("API GET /categories/", slowdown);
        return categoryRepository.findAllByOrderByTitle()
            .stream().map(CategoryDto::of).toList();
    }

    @GetMapping("recipes/{recipeId}")
    AdminRecipeDto getRecipe(@PathVariable Long recipeId, @RequestParam Optional<Long> slowdown) {

        sleepFor("API GET /recipes/" + recipeId, slowdown);

        return adminRecipeRepository.findRecipeWithFeedbackCount(recipeId)
            .map(AdminRecipeDto::of)
            .orElseThrow(() -> new EntityNotFoundException("Recipe with id '%s' not found".formatted(recipeId)))
            ;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @PutMapping("/recipes/{recipeId}")
    @Valid
    ResponseEntity<@Valid AdminRecipeDto> updateRecipe(
        @PathVariable String recipeId,
        @NotNull @Valid @RequestBody RecipeRequest updateRecipeRequest,
        @RequestParam Optional<Long> slowdown) {

        sleepFor("API PUT /recipes/" + recipeId, slowdown);


        log.info("Updating Recipe '{}': {}", recipeId, updateRecipeRequest);

        var existingRecipe = recipeRepository.findById(Long.parseLong(recipeId))
            .orElseThrow(() -> new EntityNotFoundException("Recipe with id '%s' not found".formatted(recipeId)));

        var mealType = mealtypeRepository.findById(Long.parseLong(updateRecipeRequest.mealTypeId()))
            .orElseThrow();

        var categories = updateRecipeRequest.categoryIds().stream()
            .map(Long::parseLong)
            .map(id -> categoryRepository.findById(id).orElseThrow())
            .collect(Collectors.toSet());

        var image = imageRepository.findById(Long.parseLong(updateRecipeRequest.imageId()))
            .orElseThrow(() -> new EntityNotFoundException("Image '%s' not found.".formatted(updateRecipeRequest.imageId())));

        existingRecipe.setTitle(updateRecipeRequest.title());
        existingRecipe.setHeadline(updateRecipeRequest.headline());
        existingRecipe.setPreparationTime(updateRecipeRequest.preparationTime());
        existingRecipe.setCookTime(updateRecipeRequest.cookTime());
        existingRecipe.setMealType(mealType);
        existingRecipe.setCategories(categories);
        existingRecipe.setImage(image);

        existingRecipe.removeInstructions();
        existingRecipe.removeIngredients();

        existingRecipe = recipeRepository.saveAndFlush(existingRecipe);

        for (int i = 0; i < updateRecipeRequest.instructions().size(); i++) {
            existingRecipe.addInstruction(i + 1,
                updateRecipeRequest.instructions().get(i).description()
            );
        }

        for (int i = 0; i < updateRecipeRequest.ingredients().size(); i++) {
            var ingredient = updateRecipeRequest.ingredients().get(i);
            existingRecipe.addIngredient(
                i + 1,
                ingredient.amount(),
                ingredient.unit(),
                ingredient.name()
            );
        }

        recipeRepository.save(existingRecipe);

        var feedbackCount = feedbackRepository.countByRecipeId(existingRecipe.getId());

        return ResponseEntity.ok(AdminRecipeDto.of(existingRecipe, feedbackCount));

    }

    @PostMapping("/recipes")
    @Valid
    ResponseEntity<@Valid AdminRecipeDto> createRecipe(
        @NotNull @Valid @RequestBody RecipeRequest createRecipeRequest,
        @RequestParam("slowdown") Optional<Long> slowdown) {

        sleepFor("API POST /recipes", slowdown);

        var mealType = mealtypeRepository.findById(Long.parseLong(createRecipeRequest.mealTypeId()))
            .orElseThrow();

        var categories = createRecipeRequest.categoryIds().stream()
            .map(Long::parseLong)
            .map(id -> categoryRepository.findById(id).orElseThrow())
            .collect(Collectors.toSet());

        var user = userRepository.findById(1L).orElseThrow();

        var image = imageRepository.findById(Long.parseLong(createRecipeRequest.imageId()))
            .orElseThrow(() -> new EntityNotFoundException("Image '%s' not found.".formatted(createRecipeRequest.imageId())));

        var newRecipe = new Recipe(
            user,
            createRecipeRequest.title(),
            createRecipeRequest.headline(),
            createRecipeRequest.preparationTime(),
            createRecipeRequest.cookTime(),
            mealType,
            categories,
            image
        );

        for (int i = 0; i < createRecipeRequest.instructions().size(); i++) {
            newRecipe.addInstruction(i + 1, createRecipeRequest.instructions().get(i).description());
        }

        for (int i = 0; i < createRecipeRequest.ingredients().size(); i++) {
            var ingredient = createRecipeRequest.ingredients().get(i);
            newRecipe.addIngredient(
                i + 1,
                ingredient.amount(),
                ingredient.unit(),
                ingredient.name()
            );
        }

        newRecipe = recipeRepository.save(newRecipe);

        log.info("new recipe {}", newRecipe);

        var recipeDto = AdminRecipeDto.of(newRecipe, 0);

        return ResponseEntity.ok(recipeDto);

    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageDto> uploadImage(
        @Parameter(description = "Imagefile", content = @Content(
            mediaType = "application/octet-stream",
            schema = @Schema(type = "string", format = "binary")
        ))
        @RequestPart("file") MultipartFile file,
        @Parameter(description = "Title (used for alt attribute)")
        @RequestPart("title") String title

    ) throws IOException {

        Path uploadDirectory = webConfigProps.uploadDir().startsWith("file:")
            ? Paths.get(URI.create(webConfigProps.uploadDir()))
            : Paths.get(webConfigProps.uploadDir());

        Files.createDirectories(uploadDirectory);

        String originalFilename = file.getOriginalFilename();
        String extension = ".png";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i);
        }

        var filename = "image_" + UUID.randomUUID() + extension;

        var uploadFileTarget = uploadDirectory.resolve(filename);
        log.info("Save file to '{}'", uploadFileTarget);

        Files.copy(file.getInputStream(), uploadFileTarget);

        var src = webConfigProps.uploadUrlPath() + filename;

        Image image = new Image(title, src);
        image = imageRepository.save(image);

        return ResponseEntity.ok(ImageDto.of(image));
    }

    @GetMapping("/feedback-dashboard-list")
    PageResponse<AdminFeedbackDto> getFeedbackDashboardList(@RequestParam Optional<Integer> page,
                                                           @RequestParam Optional<Integer> size,
                                                           @RequestParam("slowdown") Optional<Long> slowDown_feedback) {

        if (size.isPresent() && size.get() < 1) {
            throw new BadRequestException("Invalid argument 'size': '%s'. Must be greater than 0".formatted(size.get()));
        }

        if (page.isPresent() && page.get() < 0) {
            throw new BadRequestException("Invalid argument 'page': '%s'. Must be at least 0".formatted(page.get()));
        }

        sleepFor("API GET /admin/feedback", slowDown_feedback);

        var pageable = PageRequest.of(page.orElse(0), size.orElse(5));

        var pagedResult = feedbackRepository.getFeedbackByOrderByUpdatedAtDesc(pageable)
            .map(AdminFeedbackDto::of);

        return PageResponse.of(pagedResult);
    }


    @Schema(enumAsRef = true)
    enum NewApprovalStatus {
        APPROVED, REJECTED;

        ApprovalStatus asApprovalStatus() {
            if (this == APPROVED) {
                return ApprovalStatus.APPROVED;
            }
            return ApprovalStatus.REJECTED;
        }
    }

    record SetFeedbackApprovalStatusRequest(
        @NotNull NewApprovalStatus newApprovalStatus
    ) {
    }

    record SetFeedbackApprovalStatusResponse(
        @NotNull ApprovalStatus approvalStatus
    ) {
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @PatchMapping("feedback/{feedbackId}/approval-status")
    ResponseEntity<SetFeedbackApprovalStatusResponse> setFeedbackApprovalStatus(@PathVariable Long feedbackId, @Valid @RequestBody SetFeedbackApprovalStatusRequest setFeedbackApprovalStatusRequest, @RequestParam("slowdown") Optional<Long> slowDown) {

        sleepFor("API PATCH /admin/feedback/%s/approval-status".formatted(feedbackId), slowDown);

        feedbackRepository.updateStatus(feedbackId,
            setFeedbackApprovalStatusRequest.newApprovalStatus().asApprovalStatus()
        );

        var updatedFeedback = feedbackRepository.getById(feedbackId)
            .map(Feedback::getApprovalStatus)
            .map(SetFeedbackApprovalStatusResponse::new)
            .orElseThrow( () -> new EntityNotFoundException(
                "Feedback with id '%s' not found".formatted(feedbackId)
            ));

        return ResponseEntity.ok(updatedFeedback);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @DeleteMapping("feedback/{feedbackId}")
    ResponseEntity<Void> deleteFeedback(@PathVariable Long feedbackId, @RequestParam("slowdown") Optional<Long> slowDown) {

        sleepFor("API DELETE /admin/feedback/%s".formatted(feedbackId), slowDown);

        feedbackRepository.deleteById(feedbackId);
        return ResponseEntity.ok().build();
    }

}
