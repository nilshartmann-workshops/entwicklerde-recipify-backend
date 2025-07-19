package nh.recipify.domain.model;

import nh.recipify.domain.api.RecipeSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends Repository<Recipe, Long> {

    Recipe save(Recipe recipe);

    List<Recipe> findByUserId(Long id);

    Page<Recipe> findAllBy(Pageable p);

    Page<Recipe> findAllByIdIsIn(Pageable p, List<Long> ids);

    Page<Recipe> findAllByIdLessThan(Pageable p, long id);

    Optional<Recipe> findById(Long id);

    Page<Recipe> findAllByTitleContainsIgnoreCaseOrderByTitle(
        Pageable p,
        String title);

    Page<RecipeSummaryDto> findSummaryAllByTitleContainsIgnoreCaseOrderByTitle(
        Pageable p,
        String title);

    @Query(nativeQuery = true,
        value = """
            WITH target AS (
                SELECT id, created_at
                FROM recipes
                WHERE id = :recipeId
            ),
            older AS (
                SELECT r.id,
                       r.title,
                       mt.name AS mealType,
                       r.created_at,
                       i.id as imageId,
                       i.src AS imageSrc,
                       i.title AS imageTitle
                FROM recipes r
                JOIN meal_types mt ON r.meal_type_id = mt.id
                LEFT JOIN images i ON r.image_id = i.id,
                     target t
                WHERE r.created_at < t.created_at
                ORDER BY r.created_at DESC
                LIMIT 2
            ),
            newer AS (
                SELECT r.id,
                       r.title,
                       mt.name AS mealType,
                       r.created_at,
                       i.id as imageId,
                       i.src AS imageSrc,
                       i.title AS imageTitle
                FROM recipes r
                JOIN meal_types mt ON r.meal_type_id = mt.id
                LEFT JOIN images i ON r.image_id = i.id,
                     target t
                WHERE r.created_at > t.created_at
                ORDER BY r.created_at ASC
                LIMIT 2
            )
            SELECT id, title, mealType, imageId, imageSrc, imageTitle
            FROM (
                SELECT * FROM older
                UNION ALL
                SELECT * FROM newer
            ) combined
            ORDER BY created_at DESC;
            
            """)
    List<ExploreRecipeDto> findExploreRecipes(long recipeId);

    Recipe saveAndFlush(Recipe existingRecipe);


//    default List<RecipeSummaryDto> findExploreRecipes(long recipeId) {
//        List<Object[]> rows = _internal_findExploreRecipes(recipeId);
//        return rows.stream()
//            .map(row -> new RecipeSummaryDto(
//                (String) row[0],  // id
//                (String) row[1],  // title
//                (String) row[2]   // mealType
//            ))
//            .toList();
//    }
}
