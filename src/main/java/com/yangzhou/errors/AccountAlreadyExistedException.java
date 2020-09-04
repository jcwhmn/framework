package com.yangzhou.errors;

/**
 * 
 * @author Jiang Chuanwei
 *
 */
public class AccountAlreadyExistedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public AccountAlreadyExistedException() {
        super(ErrorConstants.LOGIN_ALREADY_USED_TYPE, "此资方已存在账号!", "userManagement", "accountexists");
    }
}
