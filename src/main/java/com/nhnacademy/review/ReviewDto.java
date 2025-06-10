package com.nhnacademy.review;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private Long userId;
    private Long bookId;
    private int rating;
    private String content;
    private LocalDateTime postedAt;
    private String photoPath;
}
