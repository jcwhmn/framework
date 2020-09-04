package com.yangzhou.errors;

import com.yangzhou.enums.StatusCode;
public class ObjectNotFoundException extends BadRequestException {
  public ObjectNotFoundException(String message) {
    super(StatusCode.objectNotFound);
    super.message = message;
  }

  public ObjectNotFoundException(Long id, String type) {
    this(String.format("ID为%d的%s不存在", id, type));
  }
}
