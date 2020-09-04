package com.yangzhou.errors;

import com.yangzhou.enums.StatusCode;

public class EmptyCollectionException extends BadRequestException {
  public EmptyCollectionException() {
    super(StatusCode.emptyCollection);
  }

  public EmptyCollectionException(String type) {
    super(StatusCode.emptyCollection);
    message = String.format("类型为%s的对象列表为空", type);
  }
}
