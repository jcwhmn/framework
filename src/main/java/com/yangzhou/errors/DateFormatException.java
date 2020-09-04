package com.yangzhou.errors;

import com.yangzhou.enums.StatusCode;

public class DateFormatException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public DateFormatException() {
        super(StatusCode.dateFormatError.value, StatusCode.dateFormatError.reasonPhrase);
    }
}
