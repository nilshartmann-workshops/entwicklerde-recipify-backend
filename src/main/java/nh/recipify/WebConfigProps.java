package nh.recipify;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@Validated
@ConfigurationProperties(prefix = "recipify.web")
public record WebConfigProps(
    @NotBlank String imagesDir,
    @NotBlank String uploadDir,
    @NotBlank String uploadUrlPath
) {

}
