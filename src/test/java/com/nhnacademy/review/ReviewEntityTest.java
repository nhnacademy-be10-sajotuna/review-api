package com.nhnacademy.review;

import com.nhnacademy.review.domain.dto.ReviewUpdateRequest;
import com.nhnacademy.review.domain.entity.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewEntityTest {

    private Review review;

    @BeforeEach
    void setUp() {
        review = Review.builder()
                .id(1L)
                .userId(1L)
                .rating(5)
                .content("원본 내용")
                .filePath("/path/original.jpg")
                .build();
    }

    @Test
    @DisplayName("update 메소드 동작 검증")
    void testUpdate() {
        // Arrange (준비)
        ReviewUpdateRequest updateRequest = new ReviewUpdateRequest(3, "수정된 내용", "/path/updated.jpg");

        // Act (실행)
        review.update(updateRequest);

        // Assert (검증)
        assertThat(review.getRating()).isEqualTo(updateRequest.getRating());
        assertThat(review.getContent()).isEqualTo(updateRequest.getContent());
        assertThat(review.getFilePath()).isEqualTo(updateRequest.getFilePath());
    }
}