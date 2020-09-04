package com.yangzhou.errors;

import com.yangzhou.enums.StatusCode;

public class JacksonConvertException extends BadRequestException {
	public JacksonConvertException(String message) {
		super(StatusCode.jacksonConvertError.value, message);
	}

	public JacksonConvertException() {
		super(StatusCode.jacksonConvertError.value, StatusCode.jacksonConvertError.reasonPhrase);
	}
}
