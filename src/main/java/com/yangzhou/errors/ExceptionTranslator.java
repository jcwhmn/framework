package com.yangzhou.errors;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import com.yangzhou.enums.StatusCode;
import com.yangzhou.util.MsgInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller advice to translate the server side exceptions to client-friendly
 * json structures. The error response follows RFC7807 - Problem Details for
 * HTTP APIs (https://tools.ietf.org/html/rfc7807)
 */
@Slf4j
@ControllerAdvice
public class ExceptionTranslator {

  @ExceptionHandler
  public ResponseEntity<MsgInfo<String>> handleBadRequestException(BadRequestException ex) {
    logException(ex);
    return MsgInfo.code(ex.getCode(), ex.getMessage(), null);
  }

  @ExceptionHandler
  public ResponseEntity<MsgInfo<String>> handleSQLException(SQLException ex) {
    logException(ex);
    return MsgInfo.code(StatusCode.sqlEx.value, "数据库异常，请稍后重试", null);
  }

  @ExceptionHandler
  public ResponseEntity<MsgInfo<Object>> ExcelRequestException(ExcelFormException e) {
    return MsgInfo.code(-231, e.getMessage(), e.getErrorRowList());
  }

  @ExceptionHandler
  public ResponseEntity<MsgInfo<List<FieldErrorVM>>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    logException(ex);
    final BindingResult result = ex.getBindingResult();
    final List<FieldErrorVM> fieldErrors = result.getFieldErrors().stream()
          .map(f -> new FieldErrorVM(f.getField(), f.getDefaultMessage()))
          .collect(Collectors.toList());

    return MsgInfo.code(StatusCode.fieldError.value, "表单验证错误", fieldErrors);
  }

  @ExceptionHandler
  public ResponseEntity<MsgInfo<String>> handleNoSuchElementException(NoSuchElementException ex,
        NativeWebRequest request) {
    logException(ex);
    return MsgInfo.code(StatusCode.noSuchElement.value, ex.getMessage(), null);
  }

  @ExceptionHandler
  public ResponseEntity<MsgInfo<String>> handleBadRequestAlertException(BadRequestAlertException ex,
        NativeWebRequest request) {
    logException(ex);
    final String errorMessage = String.format("错误警告 %s %s %s", ex.getEntityName(), ex.getErrorKey(), ex.getMessage());
    return MsgInfo.code(StatusCode.fail.value, errorMessage, null);
  }

  @ExceptionHandler
  public ResponseEntity<MsgInfo<String>> handleConcurrencyFailure(ConcurrencyFailureException ex,
        NativeWebRequest request) {
    logException(ex);
    return MsgInfo.code(StatusCode.concurrentFailure.value, "系统中发生同步错误", ex.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<MsgInfo<String>> handleUserNameNotFoundException(UsernameNotFoundException ex) {
    logException(ex);
    return MsgInfo.code(StatusCode.usernameNotFound.value, "登录名没有发现", ex.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<MsgInfo<String>> handleRunTimeExcepton(DuplicateKeyException ex) {
    logException(ex);
    return MsgInfo.code(StatusCode.duplicateEx.value, StatusCode.duplicateEx.reasonPhrase, ex.getClass().getSimpleName());
  }

  @ExceptionHandler
  public ResponseEntity<MsgInfo<String>> handleRunTimeExcepton(RuntimeException ex) {
    logException(ex);
    return MsgInfo.code(StatusCode.fail.value, "运行时异常。详细出错信息请查看系统日志。", ex.getClass().getSimpleName());
  }

  @ExceptionHandler
  public ResponseEntity<MsgInfo<String>> handleExcepton(Exception ex) {
    logException(ex);
    return MsgInfo.code(StatusCode.fail.value, "系统异常。详细出错信息请查看系统日志。", ex.getClass().getSimpleName());
  }

  @ExceptionHandler
  public ResponseEntity<MsgInfo<String>> handleInsufficientAuthenticationException(
        InsufficientAuthenticationException ex) {
    logException(ex);
    return MsgInfo.code(StatusCode.insufficientAuthentication.value, ex.getMessage(), ex.getClass().getSimpleName());
  }

  private void logException(Exception ex) {
    ex.printStackTrace();
    log.error(String.format("捕获到%s异常", ex.getClass().getSimpleName()), ex);
  }
}
