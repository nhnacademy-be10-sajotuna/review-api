package com.nhnacademy.review.exception;

import org.springframework.http.HttpStatus;

public class NotAuthorizedUserException extends ApiException {
  private static final String MESSAGE = "인가되지 않은 사용자입니다: ";
  public NotAuthorizedUserException(Long message) {
    super(HttpStatus.UNAUTHORIZED.value(), MESSAGE + message);
  }
}