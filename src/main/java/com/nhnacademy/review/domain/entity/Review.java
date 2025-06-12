package com.nhnacademy.review.domain.entity;

import com.nhnacademy.review.domain.dto.ReviewUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "content", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "posted_at", nullable = false)
    private LocalDateTime postedAt;

    @Column(name = "photo_path")
    private String photoPath;

    public void update(ReviewUpdateRequest reviewCreateRequest) {
        this.rating = reviewCreateRequest.getRating();
        this.content = reviewCreateRequest.getContent();
        this.photoPath = reviewCreateRequest.getPhotoPath();
    }
}
