package com.nhnacademy.review.repository;

import com.nhnacademy.review.domain.dto.ReviewStatsResponse;
import com.nhnacademy.review.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookId(Long bookId);
    Optional<Review> findByBookIdAndUserId(Long bookId, Long userId);

    @Query("SELECT new com.nhnacademy.review.domain.dto.ReviewStatsResponse(r.bookId, AVG(r.rating), COUNT(r)) " +
            "FROM Review r GROUP BY r.bookId HAVING COUNT(r) >= :minReviewCount ORDER BY AVG(r.rating) DESC")
    List<ReviewStatsResponse> findBooksByAverageRatingDescWithMinReviews(@Param("minReviewCount") long minReviewCount);

    @Query("SELECT new com.nhnacademy.review.domain.dto.ReviewStatsResponse(r.bookId, AVG(r.rating), COUNT(r)) " +
            "FROM Review r GROUP BY r.bookId ORDER BY COUNT(r) DESC")
    List<ReviewStatsResponse> findBooksByReviewCountDesc();

}
