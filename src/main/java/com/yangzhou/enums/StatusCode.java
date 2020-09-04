package com.yangzhou.enums;

public enum StatusCode {
  ok(1, "成功"),
  fieldError(-1, "对象属性验证错误"),
  noSuchElement(-2, "没有元素错误"),
  concurrentFailure(-3, "同步错"),
  invalidPassword(-4, "密码错"),
  usernameNotFound(-5, "用户名不存在"),
  insufficientAuthentication(-6, "权限不足"),
  dateFormatError(-7, "日期格式转换错误"),
  noObjectField(-8, "对象没有指定的属性"),
  objectNotFound(-9, "没有找到所查找的对象"),
  objectExistedAlready(-10, "对象已存在"),
  jacksonConvertError(-11, "序列化对象出错"),
  sqlEx(-12, "SQL异常"),
  duplicateEx(-13, "插入的数据有重复数据，破坏的数据的唯一性"),
  emptyCollection(-14, "空集合异常"),
  fail(0, "失败");

  public final int value;
  public final String reasonPhrase;

  StatusCode(int value, String reasonPhrase) {
    this.value = value;
    this.reasonPhrase = reasonPhrase;
  }

}
