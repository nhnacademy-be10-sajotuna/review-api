package com.nhnacademy.review.controller;

import com.nhnacademy.review.domain.dto.ReviewCreateRequest;
import com.nhnacademy.review.domain.dto.ReviewResponse;
import com.nhnacademy.review.domain.dto.ReviewUpdateRequest;
import com.nhnacademy.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/books/isbn/{isbn}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByBook(@PathVariable String isbn) {
        List<ReviewResponse> reviews = reviewService.getReviewsByIsbn(isbn);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/books/user-id/{userId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUser(@PathVariable Long userId) {
        List<ReviewResponse> reviews = reviewService.getReviewByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @ModelAttribute ReviewCreateRequest request,
            @RequestHeader("X-User-Id") Long userId) throws Exception {

        ReviewResponse created = reviewService.createReview(request, userId);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @Valid @ModelAttribute ReviewUpdateRequest request,
            @RequestHeader("X-User-Id") Long userId) throws Exception {

        ReviewResponse updated = reviewService.updateReview(id, request, userId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable("id") Long id) {
        ReviewResponse review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }
}
