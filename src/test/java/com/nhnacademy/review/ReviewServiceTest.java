package com.nhnacademy.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.review.client.BookClient;
import com.nhnacademy.review.domain.dto.ReviewCreateRequest;
import com.nhnacademy.review.domain.dto.ReviewResponse;
import com.nhnacademy.review.domain.dto.ReviewUpdateRequest;
import com.nhnacademy.review.domain.entity.Review;
import com.nhnacademy.review.exception.NotAuthorizedUserException;
import com.nhnacademy.review.exception.ReviewAlreadyExistsException;
import com.nhnacademy.review.exception.ReviewNotFoundException;
import com.nhnacademy.review.point.PointEarnRequest;
import com.nhnacademy.review.point.PointPolicyType;
import com.nhnacademy.review.repository.ReviewRepository;
import com.nhnacademy.review.service.PointMessageProducer;
import com.nhnacademy.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PointMessageProducer pointMessageProducer;

    @Mock
    private BookClient bookClient;

    private Review testReview;
    private ReviewResponse testReviewResponse;
    private Long userId = 1L;
    private String isbn = "978-89-6626-218-7";

    @BeforeEach
    void setUp() {
        testReview = Review.builder()
                .id(1L)
                .userId(userId)
                .maskedEmail("test***@nhn.com")
                .isbn(isbn)
                .bookTitle("테스트 책 제목")
                .rating(5)
                .content("정말 재미있어요!")
                .postedAt(LocalDateTime.now())
                .filePath(null)
                .build();

        testReviewResponse = new ReviewResponse(
                testReview.getId(),
                testReview.getUserId(),
                testReview.getMaskedEmail(),
                testReview.getIsbn(),
                testReview.getBookTitle(),
                testReview.getRating(),
                testReview.getContent(),
                testReview.getPostedAt(),
                testReview.getFilePath()
        );
    }

    @Test
    @DisplayName("ISBN으로 리뷰 목록 조회 성공")
    void getReviewsByIsbn_Success() {
        // Arrange (준비)
        when(reviewRepository.findByIsbn(isbn)).thenReturn(Collections.singletonList(testReview));
        when(objectMapper.convertValue(testReview, ReviewResponse.class)).thenReturn(testReviewResponse);

        // Act (실행)
        List<ReviewResponse> responses = reviewService.getReviewsByIsbn(isbn);

        // Assert (검증)
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getIsbn()).isEqualTo(isbn);
        verify(reviewRepository, times(1)).findByIsbn(isbn);
    }

    @Test
    @DisplayName("사용자 ID로 리뷰 목록 조회 성공")
    void getReviewsByUserId_Success() {
        // Arrange
        when(reviewRepository.findByUserId(userId)).thenReturn(Collections.singletonList(testReview));
        when(objectMapper.convertValue(testReview, ReviewResponse.class)).thenReturn(testReviewResponse);

        // Act
        List<ReviewResponse> responses = reviewService.getReviewsByUserId(userId);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getUserId()).isEqualTo(userId);
        verify(reviewRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("리뷰 ID로 단건 조회 성공")
    void getReviewById_Success() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(objectMapper.convertValue(testReview, ReviewResponse.class)).thenReturn(testReviewResponse);

        // Act
        ReviewResponse response = reviewService.getReviewById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 ID로 조회 시 ReviewNotFoundException 발생")
    void getReviewById_Fail_NotFound() {
        // Arrange
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.getReviewById(99L);
        });
        verify(reviewRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("리뷰 생성 성공 (이미지 없음)")
    void createReview_Success_NoImage() throws Exception {
        // Arrange
        ReviewCreateRequest request = new ReviewCreateRequest(isbn, 5, "최고예요!", "테스트 책 제목", null, "test***@nhn.com");
        when(reviewRepository.findByIsbnAndUserId(isbn, userId)).thenReturn(Optional.empty());
        when(objectMapper.convertValue(request, Review.class)).thenReturn(testReview);
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        when(objectMapper.convertValue(testReview, ReviewResponse.class)).thenReturn(testReviewResponse);

        // Act
        ReviewResponse response = reviewService.createReview(request, userId);

        // Assert
        assertThat(response.getIsbn()).isEqualTo(isbn);
        assertThat(response.getContent()).isEqualTo("정말 재미있어요!");

        // 포인트 적립 메시지 발송 검증 (이미지 없음)
        verify(pointMessageProducer, times(1)).sendPointEarnRequest(
                argThat(req -> req.getUserId().equals(userId) && req.getType() == PointPolicyType.REVIEW)
        );
        // 도서 서비스에 리뷰 생성 알림 검증
        verify(bookClient, times(1)).notifyBookReviewCreated(isbn, testReview.getRating());
    }

    @Test
    @DisplayName("리뷰 생성 성공 (이미지 포함)")
    void createReview_Success_WithImage() throws Exception {
        // Arrange
        String imagePath = "/images/review.jpg";
        ReviewCreateRequest request = new ReviewCreateRequest(isbn, 5, "최고예요!", "테스트 책 제목", imagePath, "test***@nhn.com");

        when(reviewRepository.findByIsbnAndUserId(isbn, userId)).thenReturn(Optional.empty());
        when(objectMapper.convertValue(request, Review.class)).thenReturn(testReview);
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        when(objectMapper.convertValue(testReview, ReviewResponse.class)).thenReturn(testReviewResponse);

        // Act
        ReviewResponse response = reviewService.createReview(request, userId);

        // Assert
        assertThat(response).isNotNull();

        // 포인트 적립 메시지 발송 검증 (이미지 있음)
        verify(pointMessageProducer, times(1)).sendPointEarnRequest(
                argThat(req -> req.getUserId().equals(userId) && req.getType() == PointPolicyType.REVIEW_WITH_IMAGE)
        );
        // 도서 서비스에 리뷰 생성 알림 검증
        verify(bookClient, times(1)).notifyBookReviewCreated(isbn, testReview.getRating());
    }

    @Test
    @DisplayName("이미 존재하는 리뷰 생성 시도 시 ReviewAlreadyExistsException 발생")
    void createReview_Fail_AlreadyExists() {
        // Arrange
        ReviewCreateRequest request = new ReviewCreateRequest(isbn, 5, "최고예요!", "테스트 책 제목", null, "test***@nhn.com");
        when(reviewRepository.findByIsbnAndUserId(isbn, userId)).thenReturn(Optional.of(testReview));

        // Act & Assert
        assertThrows(ReviewAlreadyExistsException.class, () -> {
            reviewService.createReview(request, userId);
        });

        // 예외 발생 시 다른 메서드들이 호출되지 않았는지 검증
        verify(pointMessageProducer, never()).sendPointEarnRequest(any(PointEarnRequest.class));
        verify(reviewRepository, never()).save(any(Review.class));
        verify(bookClient, never()).notifyBookReviewCreated(anyString(), anyInt());
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview_Success() throws Exception {
        // Arrange
        Long reviewId = 1L;
        ReviewUpdateRequest request = new ReviewUpdateRequest(4, "내용을 수정했습니다.", "/images/updated.jpg");

        // 원본 리뷰 객체를 복사해서 사용 (원본 상태 유지를 위해)
        Review modifiableReview = Review.builder().id(reviewId).userId(userId).build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(modifiableReview));
        when(objectMapper.convertValue(any(Review.class), eq(ReviewResponse.class))).thenReturn(testReviewResponse);

        // Act
        reviewService.updateReview(reviewId, request, userId);

        // Assert
        assertThat(modifiableReview.getRating()).isEqualTo(request.getRating());
        assertThat(modifiableReview.getContent()).isEqualTo(request.getContent());
        assertThat(modifiableReview.getFilePath()).isEqualTo(request.getFilePath());
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 수정 시 ReviewNotFoundException 발생")
    void updateReview_Fail_NotFound() {
        // Arrange
        Long reviewId = 99L;
        ReviewUpdateRequest request = new ReviewUpdateRequest(4, "내용 수정", null);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.updateReview(reviewId, request, userId);
        });
    }

    @Test
    @DisplayName("권한 없는 사용자가 리뷰 수정 시 NotAuthorizedUserException 발생")
    void updateReview_Fail_NotAuthorized() {
        // Arrange
        Long reviewId = 1L;
        Long otherUserId = 2L; // 다른 사용자 ID
        ReviewUpdateRequest request = new ReviewUpdateRequest(4, "내용 수정", null);

        // testReview는 userId가 1L로 설정되어 있음
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // Act & Assert
        assertThrows(NotAuthorizedUserException.class, () -> {
            // 다른 사용자(otherUserId)가 수정을 시도
            reviewService.updateReview(reviewId, request, otherUserId);
        });
    }
}