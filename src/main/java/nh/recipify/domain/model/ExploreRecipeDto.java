package nh.recipify.domain.model;

import jakarta.validation.constraints.NotNull;
import nh.recipify.domain.api.ImageDto;

public class ExploreRecipeDto {
    private final @NotNull String id;
    private final @NotNull String title;
    private final @NotNull String mealType;
    private final @NotNull ImageDto image;

    ExploreRecipeDto(long id, String title, String mealType, Long imageId, String imagePath, String imageTitle) {
        this.id = String.valueOf(id);
        this.title = title;
        this.mealType = mealType;
        this.image = imageId == null
                     || imagePath == null
                     || imageTitle == null
            ? ImageDto.emptyImage
            : new ImageDto(String.valueOf(imageId), imagePath, imageTitle);
    }

    public String getId() {
        return id;
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
