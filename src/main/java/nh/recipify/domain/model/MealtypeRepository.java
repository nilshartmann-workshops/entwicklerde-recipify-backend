package nh.recipify.domain.model;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface MealtypeRepository extends Repository<MealType, Long> {

    List<MealType> findAllByOrderByName();

    Optional<MealType> findById(Long id);
}
