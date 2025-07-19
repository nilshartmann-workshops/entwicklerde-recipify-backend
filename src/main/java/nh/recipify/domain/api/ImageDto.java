package nh.recipify.domain.api;

import jakarta.validation.constraints.NotNull;
import nh.recipify.domain.model.Image;

public record ImageDto(
    @NotNull String id,
    @NotNull String src,
    @NotNull String title
) {

    public static ImageDto of(Image image) {
        return new ImageDto(
            String.valueOf(image.getId()),
            image.getSrc(),
            image.getTitle()
        );
    }

    public static ImageDto emptyImage = new ImageDto("---", "/images/recipes/placeholder.png", "Placeholder");

}
