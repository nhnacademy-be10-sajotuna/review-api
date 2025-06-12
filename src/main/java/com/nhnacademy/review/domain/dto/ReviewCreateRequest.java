package com.nhnacademy.review.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {
    @NotNull(message = "책 ID가 유효하지 않습니다.")
    private Long bookId;

    @Min(value = 1, message = "평가 점수는 1점부터 5점까지입니다.")
    @Max(value = 5, message = "평가 점수는 1점부터 5점까지입니다.")
    private int rating;

    @NotNull(message = "내용을 입력해주세요.")
    private String content;

    private String photoPath;
}
