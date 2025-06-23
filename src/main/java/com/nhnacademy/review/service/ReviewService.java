package com.nhnacademy.review.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.review.client.BookClient;
import com.nhnacademy.review.domain.dto.ReviewCreateRequest;
import com.nhnacademy.review.domain.dto.ReviewResponse;
import com.nhnacademy.review.domain.dto.ReviewStatsResponse;
import com.nhnacademy.review.domain.dto.ReviewUpdateRequest;
import com.nhnacademy.review.domain.entity.Review;
import com.nhnacademy.review.exception.NotAuthorizedUserException;
import com.nhnacademy.review.exception.ReviewAlreadyExistsException;
import com.nhnacademy.review.exception.ReviewNotFoundException;
import com.nhnacademy.review.point.PointEarnRequest;
import com.nhnacademy.review.point.PointPolicyType;
import com.nhnacademy.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper;
    private final MinioService minioService;
    private final PointMessageProducer pointMessageProducer;
    private final BookClient bookClient;

    public List<ReviewResponse> getReviewsByBookId(String isbn) {
        List<ReviewResponse> reviewResponseList = new ArrayList<>();
        List<Review> reviews = reviewRepository.findByIsbn(isbn);
        for (Review review : reviews) {
            ReviewResponse response = objectMapper.convertValue(review, ReviewResponse.class);
            reviewResponseList.add(response);
        }
        return reviewResponseList;
    }

    public List<ReviewStatsResponse> getBooksByAverageRatingDescWithMinReviews(int minReviewCount) {
        return reviewRepository.findBooksByAverageRatingDescWithMinReviews(minReviewCount);
    }

    public List<ReviewStatsResponse> getBooksByReviewCountDesc() {
        return reviewRepository.findBooksByReviewCountDesc();
    }

    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest reviewCreateRequest, Long userId, MultipartFile file) throws Exception {
        if (reviewRepository.findByIsbnAndUserId(reviewCreateRequest.getIsbn(), userId).isPresent()) {
            throw new ReviewAlreadyExistsException(reviewCreateRequest.getIsbn());
        }

        if (file != null && !file.isEmpty()) {
            pointMessageProducer.sendPointEarnRequest(
                    new PointEarnRequest(userId, 0, PointPolicyType.REVIEW_WITH_IMAGE)
            );
            String photoPath = minioService.uploadFile(file);
            reviewCreateRequest.setPhotoPath(photoPath);
        } else {
            pointMessageProducer.sendPointEarnRequest(
                    new PointEarnRequest(userId, 0, PointPolicyType.REVIEW)
            );
        }

        Review review = objectMapper.convertValue(reviewCreateRequest, Review.class);
        review.setUserId(userId);
        Review savedReview = reviewRepository.save(review);
        bookClient.notifyBookReviewCreated(reviewCreateRequest.getIsbn(), savedReview.getId());
        return objectMapper.convertValue(savedReview, ReviewResponse.class);
    }

    @Transactional
    public ReviewResponse updateReview(Long id, ReviewUpdateRequest reviewUpdateRequest, Long userId, MultipartFile file) throws Exception {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));

        if (!review.getUserId().equals(userId)) {
            throw new NotAuthorizedUserException(userId);
        }

        if (file != null && !file.isEmpty()) {
            String photoPath = minioService.uploadFile(file);
            reviewUpdateRequest.setPhotoPath(photoPath);
        }

        String oldPhotoPath = review.getPhotoPath();
        String newPhotoPath = reviewUpdateRequest.getPhotoPath();

        if (oldPhotoPath != null && !oldPhotoPath.equals(newPhotoPath)) {
            minioService.deleteFile(oldPhotoPath);
        }

        review.update(reviewUpdateRequest);
        return objectMapper.convertValue(review, ReviewResponse.class);
    }
}
