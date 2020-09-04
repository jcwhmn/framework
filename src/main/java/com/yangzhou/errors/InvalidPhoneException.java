package com.yangzhou.errors;

public class InvalidPhoneException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public InvalidPhoneException() {
        super(ErrorConstants.INVALID_PHONE_TYPE, "手机号格式不正确!", "userManagement", "invalid phone");
    }
}
