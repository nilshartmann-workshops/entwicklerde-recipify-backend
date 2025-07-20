
package nh.recipify.domain.api.admin;

import jakarta.validation.constraints.NotNull;
import nh.recipify.domain.model.Recipe;

public record RecipeWithFeedbackCount(@NotNull Recipe recipe,
                                      @NotNull long approvedFeedbackCount,
                                      @NotNull long rejectedFeedbackCount,
                                      @NotNull long pendingFeedbackCount
                                      ) {
    public static RecipeWithFeedbackCount of(Recipe r) {
        return new RecipeWithFeedbackCount(r, 0, 0, 0);
    }

    public static RecipeWithFeedbackCount of(Recipe r,
                                             @NotNull long approvedFeedbackCount,
                                             @NotNull long rejectedFeedbackCount,
                                             @NotNull long pendingFeedbackCount) {
        return new RecipeWithFeedbackCount(r, approvedFeedbackCount, rejectedFeedbackCount, pendingFeedbackCount);
    }
}
