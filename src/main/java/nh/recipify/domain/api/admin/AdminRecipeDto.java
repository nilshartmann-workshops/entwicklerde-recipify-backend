
package nh.recipify.domain.api.admin;

import jakarta.validation.constraints.NotNull;
import nh.recipify.domain.api.ImageDto;
import nh.recipify.domain.model.Category;
import nh.recipify.domain.model.Ingredient;
import nh.recipify.domain.model.Instruction;
import nh.recipify.domain.model.Recipe;

import java.time.LocalDateTime;
import java.util.List;

record AdminRecipeDto(
    @NotNull String id,
    @NotNull LocalDateTime createdAt,
    @NotNull LocalDateTime updatedAt,
    @NotNull String title,
    @NotNull String headline,
    int preparationTime,
    int cookTime,
    @NotNull String mealTypeId,
    @NotNull List<String> categoryIds,
    @NotNull List<InstructionDto> instructions,
    @NotNull List<IngredientDto> ingredients,
    @NotNull ImageDto image,
    @NotNull int likes,
    @NotNull long approvedFeedbackCount,
    @NotNull long rejectedFeedbackCount,
    @NotNull long pendingFeedbackCount

) {

    record InstructionDto(
        @NotNull String description
    ) {
    }

    record IngredientDto(
        @NotNull double amount,
        @NotNull String unit,
        @NotNull String name
    ) {

        static IngredientDto of(Ingredient ingredient) {
            return new IngredientDto(ingredient.getAmount(), ingredient.getUnit(), ingredient.getName());
        }
    }

    static AdminRecipeDto of(Recipe recipe,
                             @NotNull long approvedFeedbackCount,
                             @NotNull long rejectedFeedbackCount,
                             @NotNull long pendingFeedbackCount
    ) {
        return new AdminRecipeDto(
            String.valueOf(recipe.getId()),
            recipe.getCreatedAt(),
            recipe.getUpdatedAt(),
            recipe.getTitle(),
            recipe.getHeadline(),
            recipe.getPreparationTime(),
            recipe.getCookTime(),
            String.valueOf(recipe.getMealType().getId()),
            recipe.getCategories().stream().map(Category::getId).map(String::valueOf).toList(),
            recipe.getInstructions().stream().map(Instruction::getDescription).map(InstructionDto::new).toList(),
            recipe.getIngredients().stream().map(IngredientDto::of).toList(),
            recipe.getImage().map(ImageDto::of).orElse(ImageDto.emptyImage),
            recipe.getLikes(),
            approvedFeedbackCount,
            rejectedFeedbackCount,
            pendingFeedbackCount
        );
    }

    static AdminRecipeDto of(RecipeWithFeedbackCount recipeWithFeedbackCount) {
        return of(
            recipeWithFeedbackCount.recipe(),
            recipeWithFeedbackCount.approvedFeedbackCount(),
            recipeWithFeedbackCount.rejectedFeedbackCount(),
            recipeWithFeedbackCount.pendingFeedbackCount()
        );
    }
}