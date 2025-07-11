package nh.recipify.domain.model;

import io.swagger.v3.oas.models.links.Link;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "recipes")
@EntityListeners(AuditingEntityListener.class)
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String headline;

    @Column(nullable = false)
    private int preparationTime;

    @Column(nullable = false)
    private int cookTime;

    @Column(name = "total_time", nullable = false, insertable = false, updatable = false)
    private int totalTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "meal_type_id", nullable = false)
    private MealType mealType;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ingredient> ingredients;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Instruction> instructions;

    @OneToMany(mappedBy = "recipe")
    private List<Feedback> feedbacks;

    @Column(name = "average_rating", precision = 10, scale = 2, nullable = false, insertable = false, updatable = false)
    private BigDecimal averageRating;

    @Column(nullable = false)
    private int likes;

    @ManyToMany
    @JoinTable(
        name = "recipe_categories",
        joinColumns = @JoinColumn(name = "recipe_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    protected Recipe() {}

    public Recipe(User user, String title, String headline, int preparationTime, int cookTime, MealType mealType, Set<Category> categories) {
        this.user = user;
        this.title = title;
        this.headline = headline;
        this.preparationTime = preparationTime;
        this.cookTime = cookTime;
        this.mealType = mealType;
        this.ingredients = new LinkedList<>();
        this.instructions = new LinkedList<>();
        this.categories = categories;
        this.feedbacks = new LinkedList<>();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getHeadline() {
        return headline;
    }

    public Integer getPreparationTime() {
        return preparationTime;
    }

    public Integer getCookTime() {
        return cookTime;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public MealType getMealType() {
        return mealType;
    }

    public List<Ingredient> getIngredients() {
        return ingredients.stream()
            .sorted(Comparator.comparingInt(Ingredient::getOrderNo))
            .collect(Collectors.toList());
    }

    public List<Instruction> getInstructions() {
        return instructions.stream()
            .sorted(Comparator.comparingInt(Instruction::getOrderNo))
            .collect(Collectors.toList());
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public int getLikes() {
        return likes;
    }

    public void likeRecipe() {
        this.likes = this.likes + 1;
    }

    public List<Category> getCategories() {
        return categories.stream()
            .sorted(Comparator.comparing((Category c) -> c.getType().getName())
                .thenComparing(Category::getTitle))
            .collect(Collectors.toList());
    }

    public void addInstruction(int orderNo, String description) {
        var instruction = new Instruction(this, orderNo, description);
        if (this.instructions == null) {
            this.instructions = new LinkedList<>();
        }

        this.instructions.add(instruction);
    }

    public void addIngredient(int orderNo, double amount, String unit, String name) {
        var ingredient = new Ingredient(
            this,
            orderNo,
            amount,
            unit,
            name
        );

        if (this.ingredients == null) {
            this.ingredients = new LinkedList<>();
        }

        this.ingredients.add(ingredient);
    }

    @Override
    public String toString() {
        return "Recipe{" +
               "id=" + id +
               ", user=" + user +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               ", title='" + title + '\'' +
               ", headline='" + headline + '\'' +
               ", preparationTime=" + preparationTime +
               ", cookTime=" + cookTime +
               ", totalTime=" + totalTime +
               ", difficulty=" + mealType +
               ", ingredients=" + ingredients +
               ", instructions=" + instructions +
               ", categories=" + categories +
               ", feedbacks=" + feedbacks +
               ", likes=" + likes +
               '}';
    }
}