package com.yangzhou.errors;

import com.yangzhou.enums.StatusCode;

public class InvalidPasswordException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public InvalidPasswordException() {
        super(StatusCode.invalidPassword.value, "无效的密码");
    }
}
