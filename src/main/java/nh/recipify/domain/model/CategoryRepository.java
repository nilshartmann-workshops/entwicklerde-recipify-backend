package nh.recipify.domain.model;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends Repository<Category, Long> {

    List<Category> findAllByOrderByTitle();
    Optional<Category> findById(Long id);

}
