package com.yangzhou.errors;


public class EmailAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyUsedException() {
        super(ErrorConstants.EMAIL_ALREADY_USED_TYPE, "邮件地址已被使用!", "userManagement", "emailexists");
    }
}
