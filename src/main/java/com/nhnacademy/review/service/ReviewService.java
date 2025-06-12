package com.nhnacademy.review.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.review.domain.dto.ReviewCreateRequest;
import com.nhnacademy.review.domain.dto.ReviewResponse;
import com.nhnacademy.review.domain.dto.ReviewUpdateRequest;
import com.nhnacademy.review.domain.entity.Review;
import com.nhnacademy.review.exception.NotAuthorizedUserException;
import com.nhnacademy.review.exception.ReviewAlreadyExistsException;
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
    public ReviewResponse createReview(ReviewCreateRequest reviewCreateRequest, Long userId) {
        if (reviewRepository.findByBookIdAndUserId(reviewCreateRequest.getBookId(), userId).isPresent()) {
            throw new ReviewAlreadyExistsException(reviewCreateRequest.getBookId());
        }
        Review review = objectMapper.convertValue(reviewCreateRequest, Review.class);
        review.setUserId(userId);
        Review savedReview = reviewRepository.save(review);
        return objectMapper.convertValue(savedReview, ReviewResponse.class);
    }

    @Transactional
    public ReviewResponse updateReview(Long id, ReviewUpdateRequest reviewUpdateRequest, Long userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        if (!review.getUserId().equals(userId)) {
            throw new NotAuthorizedUserException(userId);
        }
        review.update(reviewUpdateRequest);
        return objectMapper.convertValue(review, ReviewResponse.class);
    }
}
