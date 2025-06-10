package com.nhnacademy.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public List<Review> getReviewsByBookId(Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    @Transactional
    public Review createReview(ReviewDto reviewDto) {
        Review review = Review.builder()
                .userId(reviewDto.getUserId())
                .bookId(reviewDto.getBookId())
                .rating(reviewDto.getRating())
                .content(reviewDto.getContent())
                .postedAt(reviewDto.getPostedAt())
                .photoPath(reviewDto.getPhotoPath())
                .build();
        return reviewRepository.save(review);
    }

    @Transactional
    public Review updateReview(Long id, ReviewDto reviewDto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        review.setUserId(reviewDto.getUserId());
        review.setBookId(reviewDto.getBookId());
        review.setRating(reviewDto.getRating());
        review.setContent(reviewDto.getContent());
        review.setPostedAt(reviewDto.getPostedAt());
        review.setPhotoPath(reviewDto.getPhotoPath());
        return reviewRepository.save(review);
    }
}
