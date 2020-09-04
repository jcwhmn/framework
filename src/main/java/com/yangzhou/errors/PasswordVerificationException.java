package com.yangzhou.errors;

public class PasswordVerificationException extends BadRequestAlertException{
  public PasswordVerificationException() {
    super(ErrorConstants.INVALID_PASSWORD_TYPE, "无效的密码!", "userManagement", "invalidPassword");
  }

  public PasswordVerificationException(String message) {
    super(ErrorConstants.INVALID_PASSWORD_TYPE, message, "userManagement", "invalidPassword");
  }
}
