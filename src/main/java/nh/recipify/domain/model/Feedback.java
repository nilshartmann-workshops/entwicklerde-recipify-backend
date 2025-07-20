package nh.recipify.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "feedbacks")
@EntityListeners(AuditingEntityListener.class)
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @NotNull
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @NotNull
    private String commenter;

    @Column(nullable = false)
    @NotNull
    private Integer rating;

    @Column(nullable = false)
    @NotNull
    private String comment;

    @Column(nullable = false, name = "approval_status")
    @Enumerated(value = EnumType.STRING)
    ApprovalStatus approvalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    @JsonIgnore
    private Recipe recipe;

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCommenter() {
        return commenter;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    protected Feedback() {
    }

    public Feedback(Recipe recipe, String commenter, Integer rating, String comment) {
        this.createdAt = LocalDateTime.now();
        this.recipe = recipe;
        this.commenter = commenter;
        this.rating = rating;
        this.comment = comment;
        this.approvalStatus = ApprovalStatus.PENDING;
    }

    @Override
    public String toString() {
        return "Feedback{" +
               "id=" + id +
               ", createdAt=" + createdAt +
               ", commenter='" + commenter + '\'' +
               ", rating=" + rating +
               ", comment='" + comment + '\'' +
               ", status=" + approvalStatus +
               ", recipe=" + recipe +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Feedback feedback)) return false;
        return Objects.equals(id, feedback.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}