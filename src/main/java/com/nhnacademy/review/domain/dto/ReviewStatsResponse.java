package com.nhnacademy.review.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewStatsResponse {
    private Long bookId;
    private Double averageRating;
    private Long reviewCount;
}

