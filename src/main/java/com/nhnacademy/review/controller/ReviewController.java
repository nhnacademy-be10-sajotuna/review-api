package com.nhnacademy.review.controller;

import com.nhnacademy.review.domain.dto.ReviewRequest;
import com.nhnacademy.review.domain.dto.ReviewResponse;
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

    @GetMapping("/books/{bookId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByBook(@PathVariable Long bookId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request, @RequestHeader("X-User-Id")Long userId) {
        ReviewResponse created = reviewService.createReview(request, userId);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewRequest request, @RequestHeader("X-User-Id")Long userId) {
        ReviewResponse updated = reviewService.updateReview(id, request, userId);
        return ResponseEntity.ok(updated);
    }
}
