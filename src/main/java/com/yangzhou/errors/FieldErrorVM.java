package com.yangzhou.errors;

import java.io.Serializable;

import lombok.Getter;
import lombok.ToString;

@Getter
public class FieldErrorVM implements Serializable {
	private static final long serialVersionUID = 6279484402377139358L;

	private final String field;

    private final String message;

    public FieldErrorVM(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
