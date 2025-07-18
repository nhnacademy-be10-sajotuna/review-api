package com.nhnacademy.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.review.controller.ReviewController;
import com.nhnacademy.review.domain.dto.ReviewCreateRequest;
import com.nhnacademy.review.domain.dto.ReviewResponse;
import com.nhnacademy.review.domain.dto.ReviewUpdateRequest;
import com.nhnacademy.review.exception.ReviewNotFoundException;
import com.nhnacademy.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(ReviewController.class) // ReviewController를 웹 계층 테스트 대상으로 지정
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTP 요청을 시뮬레이션하는 객체

    @Autowired
    private ObjectMapper objectMapper; // 객체를 JSON으로 변환

    @MockitoBean
    private ReviewService reviewService; // Controller가 의존하는 Service를 Mock 객체로 대체

    private ReviewResponse testReviewResponse;
    private Long userId = 1L;
    private Long reviewId = 1L;
    private String isbn = "978-89-6626-218-7";

    @BeforeEach
    void setUp() {
        // 모든 테스트에서 공통으로 사용할 응답 객체 초기화
        testReviewResponse = new ReviewResponse(
                reviewId,
                userId,
                "test***@nhn.com",
                isbn,
                "테스트 책 제목",
                5,
                "정말 재미있어요!",
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("GET /api/reviews/books/isbn/{isbn} - ISBN으로 리뷰 목록 조회 성공")
    void getReviewsByBook_Success() throws Exception {
        // Arrange (준비)
        List<ReviewResponse> responseList = Collections.singletonList(testReviewResponse);
        given(reviewService.getReviewsByIsbn(isbn)).willReturn(responseList);

        // Act & Assert (실행 및 검증)
        mockMvc.perform(get("/api/reviews/books/isbn/{isbn}", isbn))
                .andExpect(status().isOk()) // HTTP 상태 코드 200 (OK) 확인
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // 응답 컨텐츠 타입 확인
                .andExpect(jsonPath("$[0].isbn").value(isbn)); // JSON 응답의 첫 번째 객체의 isbn 필드 값 확인
    }

    @Test
    @DisplayName("GET /api/reviews/books/user-id/{userId} - 사용자 ID로 리뷰 목록 조회 성공")
    void getReviewsByUser_Success() throws Exception {
        // Arrange
        List<ReviewResponse> responseList = Collections.singletonList(testReviewResponse);
        given(reviewService.getReviewsByUserId(userId)).willReturn(responseList);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/books/user-id/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].userId").value(userId));
    }

    @Test
    @DisplayName("GET /api/reviews/{id} - 리뷰 ID로 단건 조회 성공")
    void getReviewById_Success() throws Exception {
        // Arrange
        given(reviewService.getReviewById(reviewId)).willReturn(testReviewResponse);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(reviewId))
                .andExpect(jsonPath("$.content").value("정말 재미있어요!"));
    }

    @Test
    @DisplayName("GET /api/reviews/{id} - 존재하지 않는 리뷰 조회 시 404 Not Found 응답")
    void getReviewById_Fail_NotFound() throws Exception {
        // Arrange
        // reviewService.getReviewById 호출 시 ReviewNotFoundException 예외를 던지도록 설정
        given(reviewService.getReviewById(anyLong())).willThrow(new ReviewNotFoundException(99L));

        // Act & Assert
        mockMvc.perform(get("/api/reviews/{id}", 99L))
                .andExpect(status().isNotFound()); // AdviceController에 의해 404 상태 코드가 반환되는지 확인
    }

    @Test
    @DisplayName("POST /api/reviews - 리뷰 생성 성공")
    void createReview_Success() throws Exception {
        // Arrange
        ReviewCreateRequest createRequest = new ReviewCreateRequest(isbn, 5, "새로운 리뷰 내용", "책 제목", null, "new***@nhn.com");
        given(reviewService.createReview(any(ReviewCreateRequest.class), eq(userId))).willReturn(testReviewResponse);

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED) // @ModelAttribute는 form 데이터로 전송
                        .param("isbn", createRequest.getIsbn())
                        .param("rating", String.valueOf(createRequest.getRating()))
                        .param("content", createRequest.getContent())
                        .header("X-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId));
    }

    @Test
    @DisplayName("POST /api/reviews - 유효성 검사 실패 시 400 Bad Request 응답")
    void createReview_Fail_Validation() throws Exception {
        // Arrange
        // content가 비어있는 잘못된 요청
        ReviewCreateRequest invalidRequest = new ReviewCreateRequest(isbn, 5, "", "책 제목", null, "new***@nhn.com");

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("isbn", invalidRequest.getIsbn())
                        .param("rating", String.valueOf(invalidRequest.getRating()))
                        .param("content", invalidRequest.getContent()) // @NotBlank 위반
                        .header("X-User-Id", String.valueOf(userId)))
                .andExpect(status().isBadRequest()); // AdviceController에 의해 400 상태 코드가 반환되는지 확인
    }

    @Test
    @DisplayName("PUT /api/reviews/{id} - 리뷰 수정 성공")
    void updateReview_Success() throws Exception {
        // Arrange
        ReviewUpdateRequest updateRequest = new ReviewUpdateRequest(4, "수정된 리뷰 내용", null);
        given(reviewService.updateReview(eq(reviewId), any(ReviewUpdateRequest.class), eq(userId))).willReturn(testReviewResponse);

        // Act & Assert
        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("rating", String.valueOf(updateRequest.getRating()))
                        .param("content", updateRequest.getContent())
                        .header("X-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId));
    }

    @Test
    @DisplayName("PUT /api/reviews/{id} - 유효성 검사 실패 시 (평점 범위 초과) 400 Bad Request 응답")
    void updateReview_Fail_Validation() throws Exception {
        // Arrange
        ReviewUpdateRequest invalidRequest = new ReviewUpdateRequest(10, "평점이 잘못됨", null); // @Max(5) 위반

        // Act & Assert
        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("rating", String.valueOf(invalidRequest.getRating()))
                        .param("content", invalidRequest.getContent())
                        .header("X-User-Id", String.valueOf(userId)))
                .andExpect(status().isBadRequest());
    }
}