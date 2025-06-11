package com.nhnacademy.review.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long userId;
    private Long bookId;
    private int rating;
    private String content;
    private LocalDateTime postedAt;
    private String photoPath;
}
