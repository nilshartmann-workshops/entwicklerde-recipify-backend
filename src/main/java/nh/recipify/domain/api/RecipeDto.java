package nh.recipify.domain.api;

import jakarta.validation.constraints.NotNull;
import nh.recipify.domain.model.Feedback;
import nh.recipify.domain.model.Recipe;

import java.time.LocalDateTime;
import java.util.List;

import static nh.recipify.domain.api.ImageDto.emptyImage;

public record RecipeDto(

    @NotNull String id,
    @NotNull LocalDateTime createdAt,
    @NotNull String userFullname,
    @NotNull String title,
    @NotNull String headline,
    @NotNull int preparationTime,
    @NotNull int cookTime,
    @NotNull List<CategoryDto> categories,
    @NotNull String mealType,
    @NotNull int likes,
    @NotNull ImageDto image,
    @NotNull LocalDateTime generatedAt

) {

    public static RecipeDto forRecipe(Recipe r) {
        return new RecipeDto(
            r.getId().toString(),
            r.getCreatedAt(),
            r.getUser().getFullname(),
            r.getTitle(),
            r.getHeadline(),
            r.getPreparationTime(),
            r.getCookTime(),
            r.getCategories().stream().map(CategoryDto::of).toList(),
            r.getMealType().getName(),
            r.getLikes(),
            r.getImage().map(ImageDto::of).orElse(emptyImage),
            LocalDateTime.now()
        );
    }

}
