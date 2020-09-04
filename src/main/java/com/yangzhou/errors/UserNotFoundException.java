package com.yangzhou.errors;

public class UserNotFoundException extends BadRequestAlertException {

	private static final long serialVersionUID = 1L;

    public UserNotFoundException() {
        super("用户不存在!", "userManagement", "accountexists");
    }
}
