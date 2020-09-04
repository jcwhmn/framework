package com.yangzhou.errors;

import com.yangzhou.enums.StatusCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BadRequestException extends RuntimeException {
  private static final long serialVersionUID = -6072147852453980078L;
  protected int             code;
  protected String          message;
  protected Object          object           = null;

  public BadRequestException(StatusCode statusCode) {
    this.code = statusCode.value;
    this.message = statusCode.reasonPhrase;
  }

  public BadRequestException(int code, String message) {
    this.code = code;
    this.message = message;
  }
}
