package com.nhnacademy.review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewDto {
    private Long id;
    private Long bookId;
    private Long userId;
    private int rating;
    private String reviewText;
    private String photoPath;
}
