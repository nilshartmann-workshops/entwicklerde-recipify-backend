package nh.recipify.domain.model;

import jakarta.validation.constraints.NotNull;
import nh.recipify.domain.api.RecipeSummaryDto;

public class ExploreRecipeDto {
    private final @NotNull String id;
    private final @NotNull String title;
    private final @NotNull String mealType;
    private final @NotNull String image;

    ExploreRecipeDto(long id, String title, String mealType, String image) {
        this.id = String.valueOf(id);
        this.title = title;
        this.mealType = mealType;
        this.image = image;
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

    public String getImage() {
        if (image == null) {
            return "/images/recipes/placeholder.png";
        }
        return image;
    }
}
