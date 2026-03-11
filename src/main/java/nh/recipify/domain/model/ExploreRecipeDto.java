package nh.recipify.domain.model;

import jakarta.validation.constraints.NotNull;
import nh.recipify.domain.api.ImageDto;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ExploreRecipeDto {
    private final @NotNull String id;
    private final @NotNull LocalDateTime generatedAt;
    private final @NotNull String title;
    private final @NotNull String mealType;
    private final @NotNull ImageDto image;

    ExploreRecipeDto(long id, Instant generatedAt, String title, String mealType, Long imageId, String imageSrc, String imageTitle) {
        this.id = String.valueOf(id);
        this.generatedAt = generatedAt.atZone(ZoneId.of("Europe/Berlin")).toLocalDateTime();
        this.title = title;
        this.mealType = mealType;
        this.image = imageId == null
                     || imageSrc == null
                     || imageTitle == null
            ? ImageDto.emptyImage
            : new ImageDto(String.valueOf(imageId), imageSrc, imageTitle);
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getMealType() {
        return mealType;
    }

    public ImageDto getImage() {
        return image;
    }
}
