
package nh.recipify.domain.api.admin;

import jakarta.validation.constraints.NotNull;

import java.util.List;

record RecipeRequest(
    @NotNull String title,
    @NotNull String headline,
    int preparationTime,
    int cookTime,
    @NotNull String mealTypeId,
    @NotNull List<String> categoryIds,
    @NotNull List<InstructionRequest> instructions,
    @NotNull List<IngredientRequest> ingredients,
    @NotNull String imageId
) {

    record InstructionRequest(
        @NotNull String description
    ) {}

    record IngredientRequest(
        @NotNull double amount,
        @NotNull String unit,
        @NotNull String name
    ) {
    }
}
