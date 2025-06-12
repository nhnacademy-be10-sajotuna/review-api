package com.nhnacademy.review.exception;

import org.springframework.http.HttpStatus;

public class ReviewNotFoundException extends ApiException {
    private static final String MESSAGE = "리뷰가 존재하지 않습니다: ";
    public ReviewNotFoundException(Long message) {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE + message);
    }
}
