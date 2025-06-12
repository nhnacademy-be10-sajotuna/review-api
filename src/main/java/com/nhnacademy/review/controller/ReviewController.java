package com.nhnacademy.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.review.domain.dto.ReviewResponse;

import com.nhnacademy.review.exception.IllegalVariableException;
import com.nhnacademy.review.service.ReviewService;
import com.nhnacademy.review.domain.dto.ReviewRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ObjectMapper objectMapper;

    @GetMapping("/books/{bookId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByBook(@PathVariable Long bookId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalVariableException(bindingResult.getFieldError().getDefaultMessage());
        }
        ReviewResponse created = reviewService.createReview(request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalVariableException(bindingResult.getFieldError().getDefaultMessage());
        }
        ReviewResponse updated = reviewService.updateReview(id, request);
        return ResponseEntity.ok(updated);
    }
}
