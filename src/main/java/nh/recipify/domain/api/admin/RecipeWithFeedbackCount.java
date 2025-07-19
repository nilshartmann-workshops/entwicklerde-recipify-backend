
package nh.recipify.domain.api.admin;

import jakarta.validation.constraints.NotNull;
import nh.recipify.domain.model.Recipe;

public record RecipeWithFeedbackCount(@NotNull Recipe recipe, @NotNull long feedbackCount) {
    public static RecipeWithFeedbackCount of(Recipe r) {
        return new RecipeWithFeedbackCount(r, 0);
    }

    public static RecipeWithFeedbackCount of(Recipe r, long feedbackCount) {
        return new RecipeWithFeedbackCount(r, feedbackCount);
    }
}
