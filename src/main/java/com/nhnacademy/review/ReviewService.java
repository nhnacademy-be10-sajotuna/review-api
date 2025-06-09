package com.nhnacademy.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewDto saveReview(Long bookId, Long userId, int rating, String reviewText, MultipartFile photo) {
        // 실제로는 MultipartFile을 파일 저장소(예: S3, 로컬)에 저장하고, 파일 경로를 저장하는 것이 일반적입니다.
        String photoPath = null;
        if (photo != null && !photo.isEmpty()) {
            // 파일 저장 로직 (예시: "uploads/reviews/{userId}_{bookId}_{timestamp}.jpg")
            // 실제 구현은 파일 저장 서비스에 따라 다릅니다.
            photoPath = String.format("uploads/reviews/%d_%d_%d.jpg", userId, bookId, System.currentTimeMillis());
            // 파일 저장 코드는 생략 (예: Files.copy(photo.getInputStream(), ...))
        }

        // DTO를 엔티티로 변환하여 저장
        Review review = Review.builder()
                .bookId(bookId)
                .userId(userId)
                .rating(rating)
                .reviewText(reviewText)
                .photoPath(photoPath)
                .build();

        Review savedReview = reviewRepository.save(review);
        return convertToDto(savedReview);
    }

    public List<ReviewDto> getReviewsByBookId(Long bookId) {
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        return reviews.stream()
                .map(this::convertToDto)
                .toList();
    }

    private ReviewDto convertToDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .bookId(review.getBookId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .photoPath(review.getPhotoPath())
                .build();
    }
}
