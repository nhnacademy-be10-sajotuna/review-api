package com.nhnacademy.review;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<String> addReview(@PathVariable Long bookId,
                                            @RequestParam int rating,
                                            @RequestParam(required = false) MultipartFile photo,
                                            @RequestParam String reviewText,
                                            @RequestHeader("userId") Long userId) {
        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body("평가 점수는 1~5점만 가능합니다.");
        }
        try {
            // 리뷰 저장
            ReviewDto review = reviewService.saveReview(bookId, userId, rating, reviewText, photo);

            return ResponseEntity.ok("리뷰가 등록되었습니다. 포인트가 적립되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 등록에 실패했습니다.");
        }
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable Long bookId) {
        List<ReviewDto> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }
}
