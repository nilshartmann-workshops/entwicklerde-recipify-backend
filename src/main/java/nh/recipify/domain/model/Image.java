package nh.recipify.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "images")
@EntityListeners(AuditingEntityListener.class)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    private String title;

    @NotNull
    private String src;

    protected Image(){}

    public Image(String title, String src) {
        this.title = title;
        this.src = src;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSrc() {
        return src;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Image image)) return false;
        return Objects.equals(id, image.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}