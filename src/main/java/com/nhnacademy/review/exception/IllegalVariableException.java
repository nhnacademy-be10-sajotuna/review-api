package com.nhnacademy.review.exception;

import org.springframework.http.HttpStatus;

public class IllegalVariableException extends ApiException {
  private static final String MESSAGE = "잘못된 변수입니다: ";
  public IllegalVariableException(String message) {
    super(HttpStatus.BAD_REQUEST.value(), MESSAGE + message);
  }
}
