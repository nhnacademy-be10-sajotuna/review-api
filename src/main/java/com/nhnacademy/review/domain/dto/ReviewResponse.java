package com.nhnacademy.review.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long userId;
    private String isbn;
    private int rating;
    private String content;
    private LocalDateTime postedAt;
    private String photoPath;
}
