package com.nhnacademy.review.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long userId;
    private String maskedEmail;
    private String isbn;
    private String bookTitle;
    private int rating;
    private String content;
    private LocalDateTime postedAt;
    private String filePath;
}
