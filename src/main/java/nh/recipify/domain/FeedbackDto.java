package nh.recipify.domain;

import jakarta.validation.constraints.NotNull;
import nh.recipify.domain.model.Feedback;

import java.time.LocalDateTime;

public record FeedbackDto(@NotNull String id, @NotNull LocalDateTime createdAt, @NotNull String commenter,
                          @NotNull Integer rating, @NotNull String comment) {
    public static FeedbackDto of(Feedback feedback) {

        return new FeedbackDto(
            String.valueOf(feedback.getId()),
            feedback.getCreatedAt(),
            feedback.getCommenter(),
            feedback.getRating(),
            feedback.getComment()
        );

    }
}
