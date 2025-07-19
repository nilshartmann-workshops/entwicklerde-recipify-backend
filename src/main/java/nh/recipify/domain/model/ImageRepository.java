package nh.recipify.domain.model;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends Repository<Image, Long> {
    List<Image> findAllByOrderByCreatedAtDescTitle();

    Image save(Image image);

    Optional<Image> findById(Long id);
}
