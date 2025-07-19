package nh.recipify.domain.api.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public interface LatestRecipeDto {
    @NotNull String getId();
    @NotNull LocalDateTime getCreatedAt();
    @NotNull LocalDateTime getUpdatedAt();
    @NotNull String getTitle();
    @NotNull String getHeadline();
    @NotNull long getLikes();
    @NotNull long getFeedbackCount();
}


