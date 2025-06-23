package com.nhnacademy.review.repository;

import com.nhnacademy.review.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByIsbn(String isbn);
    Optional<Review> findByIsbnAndUserId(String isbn, Long userId);
}
