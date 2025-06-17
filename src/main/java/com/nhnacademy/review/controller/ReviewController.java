package com.nhnacademy.review.controller;

import com.nhnacademy.review.domain.dto.ReviewCreateRequest;
import com.nhnacademy.review.domain.dto.ReviewResponse;
import com.nhnacademy.review.domain.dto.ReviewUpdateRequest;
import com.nhnacademy.review.service.MinioService;
import com.nhnacademy.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final MinioService minioService;

    @GetMapping("/books/{bookId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByBook(@PathVariable Long bookId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @ModelAttribute ReviewCreateRequest request,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestHeader("X-User-Id") Long userId) throws Exception {

        ReviewResponse created = reviewService.createReview(request, userId, file);
        return ResponseEntity.ok(created);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @Valid @ModelAttribute ReviewUpdateRequest request,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestHeader("X-User-Id") Long userId) throws Exception {

        ReviewResponse updated = reviewService.updateReview(id, request, userId, file);
        return ResponseEntity.ok(updated);
    }

}
