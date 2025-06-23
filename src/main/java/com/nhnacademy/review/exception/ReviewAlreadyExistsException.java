package com.nhnacademy.review.exception;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;

public class ReviewAlreadyExistsException extends ApiException {
  private static final String MESSAGE = "이미 해당 도서에 대한 리뷰가 존재합니다: ";
  public ReviewAlreadyExistsException(@NotNull(message = "책 ID가 유효하지 않습니다.") String isbn) {
    super(HttpStatus.CONFLICT.value(), MESSAGE + isbn);
  }
}
