package nh.recipify.domain.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends Repository<Feedback, Long> {

    @Deprecated
    List<Feedback> getFeedbackByRecipeIdOrderByCreatedAtDesc(Long recipeId);

    Page<Feedback> getFeedbackByRecipeIdAndApprovalStatusOrderByCreatedAtDesc(Long recipeId, ApprovalStatus status, Pageable pageable);

    /**
     * Für Admin API
     * @param pageable
     * @return
     */
    Page<Feedback> getFeedbackByOrderByUpdatedAtDesc(Pageable pageable);

    Optional<Feedback> getById(Long id);


    Feedback save(Feedback feedback);

    void deleteById(Long id);

    @Modifying
    @Query(nativeQuery = true,
        value = """
            update feedbacks set approval_status = :#{#approvalStatus.name()}, updated_at = now() where id = :id
"""
    )
    void updateStatus(Long id, ApprovalStatus approvalStatus);

    @Query(nativeQuery = true,
    value = """
        select avg(rating) from feedbacks where recipe_id = :recipeId
    """)
    float averageRating(Long recipeId);

    Long countByRecipeId(Long recipeId);

}
