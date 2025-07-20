package nh.recipify.domain.api.admin;

import nh.recipify.domain.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

interface AdminRecipeRepository extends Repository<Recipe, Long> {

//    @Query(nativeQuery = true,
//        countQuery = "SELECT COUNT(*) FROM recipes",
//        value = """
//            SELECT
//                r.id::text,
//                r.created_at::timestamp,
//                r.updated_at::timestamp,
//                r.title,
//                r.headline,
//                r.likes::bigint,
//                (SELECT COUNT(f.id)::bigint FROM feedbacks f WHERE f.recipe_id = r.id) as feedbackCount
//            FROM
//                recipes r
//            ORDER BY
//                r.created_at DESC
//            LIMIT :#{#pageable.pageSize}
//            OFFSET :#{#pageable.offset}
//            """)
//    Page<LatestRecipeDto> findLatestRecipes(Pageable pageable);

    @Query(value = """
            SELECT r AS recipe,
                   (SELECT COUNT(f) FROM Feedback f WHERE f.approvalStatus = 'APPROVED' and f.recipe = r) AS approvedFeedbackCount,
                   (SELECT COUNT(f) FROM Feedback f WHERE f.approvalStatus = 'PENDING' and f.recipe = r) AS approvedFeedbackPending,
                   (SELECT COUNT(f) FROM Feedback f WHERE f.approvalStatus = 'REJECTED' and f.recipe = r) AS approvedFeedbackRejected                               
            FROM Recipe r
            ORDER BY r.createdAt DESC
            """,
        countQuery = "SELECT COUNT(r) FROM Recipe r")
    Page<RecipeWithFeedbackCount> findRecipesWithFeedbackCount(Pageable pageable);

    @Query(value = """
            SELECT r AS recipe, 
                   (SELECT COUNT(f) FROM Feedback f WHERE f.approvalStatus = 'APPROVED' and f.recipe = r) AS approvedFeedbackCount,
                   (SELECT COUNT(f) FROM Feedback f WHERE f.approvalStatus = 'REJECTED' and f.recipe = r) AS approvedFeedbackRejected,                               
                   (SELECT COUNT(f) FROM Feedback f WHERE f.approvalStatus = 'PENDING' and f.recipe = r) AS approvedFeedbackPending
            FROM Recipe r
            WHERE r.id = :id            
            """)
    Optional<RecipeWithFeedbackCount> findRecipeWithFeedbackCount(long id);

}
