package com.nhnacademy.review.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long bookId;

    @Min(1)
    @Max(5)
    private int rating;

    @NotNull
    private String content;
    
    @NotNull
    private LocalDateTime postedAt;

    private String photoPath;
}
