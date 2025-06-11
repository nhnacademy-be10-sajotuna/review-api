package com.nhnacademy.review.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.review.domain.dto.ReviewRequest;
import com.nhnacademy.review.domain.dto.ReviewResponse;
import com.nhnacademy.review.domain.entity.Review;
import com.nhnacademy.review.exception.ReviewNotFoundException;
import com.nhnacademy.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper;

    public List<ReviewResponse> getReviewsByBookId(Long bookId) {
        List<ReviewResponse> reviewResponseList = new ArrayList<>();
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        for (Review review : reviews) {
            ReviewResponse response = objectMapper.convertValue(review, ReviewResponse.class);
            reviewResponseList.add(response);
        }
        return reviewResponseList;
    }

    @Transactional
    public ReviewResponse createReview(ReviewRequest reviewRequest) {
        Review review = objectMapper.convertValue(reviewRequest, Review.class);
        Review savedReview = reviewRepository.save(review);
        return objectMapper.convertValue(savedReview, ReviewResponse.class);
    }

    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest reviewRequest) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id.toString()));
        review.setUserId(reviewRequest.getUserId());
        review.setBookId(reviewRequest.getBookId());
        review.setRating(reviewRequest.getRating());
        review.setContent(reviewRequest.getContent());
        review.setPostedAt(reviewRequest.getPostedAt());
        review.setPhotoPath(reviewRequest.getPhotoPath());
        return objectMapper.convertValue(review, ReviewResponse.class);
    }
}
