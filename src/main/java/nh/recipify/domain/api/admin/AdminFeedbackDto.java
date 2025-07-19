package nh.recipify.domain.api.admin;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import nh.recipify.domain.FeedbackDto;
import nh.recipify.domain.api.RecipeDto;
import nh.recipify.domain.model.ApprovalStatus;
import nh.recipify.domain.model.Feedback;

import java.time.LocalDateTime;

public record AdminFeedbackDto(@JsonUnwrapped @Valid FeedbackDto feedback,
                               @NotNull LocalDateTime updatedAt,
                               @NotNull ApprovalStatus approvalStatus,
                               @NotNull String recipeId,
                               @NotNull String recipeTitle
                               ) {

    public static AdminFeedbackDto of(Feedback feedback) {
        return new AdminFeedbackDto(
            FeedbackDto.of(feedback),
            feedback.getUpdatedAt(),
            feedback.getApprovalStatus(),
            String.valueOf(feedback.getRecipe().getId()),
            feedback.getRecipe().getTitle()
        );
    }

}
