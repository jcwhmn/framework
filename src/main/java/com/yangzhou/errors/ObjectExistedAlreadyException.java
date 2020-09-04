package com.yangzhou.errors;

import com.yangzhou.enums.StatusCode;

public class ObjectExistedAlreadyException extends BadRequestException {
  public ObjectExistedAlreadyException(String message) {
    super(StatusCode.objectExistedAlready.value, message);
  }

  public ObjectExistedAlreadyException(Long id, String type) {
    super(StatusCode.objectExistedAlready.value, String.format("ID为%d的%s已存在", id, type));
  }
}
