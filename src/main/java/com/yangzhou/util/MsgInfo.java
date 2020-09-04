package com.yangzhou.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.yangzhou.enums.StatusCode;
import com.yangzhou.security.SecurityUtils;
import com.yangzhou.security.jwt.JWTFilter;
import lombok.Getter;

@Getter
public class MsgInfo<T> {
  private int code;
  private String msg;
  private T data;

  public MsgInfo() {

  }

  public MsgInfo(StatusCode code, String msg, T data) {
    this(code.value, msg, data);
  }

  public MsgInfo(StatusCode code, T data) {
    this(code.value, code.reasonPhrase, data);
  }

  public MsgInfo(int code, String msg, T data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
  }

  public static <T> ResponseEntity<MsgInfo<T>> code(Integer code, String msg, T data) {
    final MsgInfo<T> msgInfo = new MsgInfo<>(code, msg, data);
    // 判断Token是否被刷新，如果已刷新，将token加到header中返回前端
    if (SecurityUtils.isRefreshed()) {
      final String jwt = SecurityUtils.getCurrentJWTString();
      final HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
      return new ResponseEntity<>(msgInfo, httpHeaders, HttpStatus.OK);
    } else
      return ResponseEntity.ok(msgInfo);
  }

  public static <T> ResponseEntity<MsgInfo<T>> ok(String msg, T data) {
    return code(StatusCode.ok.value, msg, data);
  }

  public static <T> ResponseEntity<MsgInfo<T>> ok(T data) {
    return code(StatusCode.ok.value, StatusCode.ok.reasonPhrase, data);
  }

  public static <T> ResponseEntity<MsgInfo<T>> fail(String msg, T data) {
    return code(StatusCode.fail.value, msg, data);
  }

  public static <T> ResponseEntity<MsgInfo<T>> fail(T data) {
    return code(StatusCode.fail.value, StatusCode.fail.reasonPhrase, data);
  }

}
