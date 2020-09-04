package com.yangzhou.aop.logging;

import java.lang.reflect.Method;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.yangzhou.frame.service.SysLogService;
import com.yangzhou.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class SysLogAspect {

  private static String message;
  private static long startTime;
  private static boolean isOr = true; // 默认执行日志

  @Resource
  private SysLogService logService;

  @Pointcut("@annotation(sysLogAnnotation)")
  public void sysLogAop(SysLogAnnotation sysLogAnnotation) {
  }

  // 进入方法时的时间戳
  @Before("sysLogAop(sysLogAnnotation)")
  public void before(JoinPoint point, SysLogAnnotation sysLogAnnotation) {
    startTime = System.currentTimeMillis();
  }

  // 方法结束时拼凑操作描述
  @After("sysLogAop(sysLogAnnotation)")
  public void after(JoinPoint point, SysLogAnnotation sysLogAnnotation) {
    if (!StringUtils.isEmpty(sysLogAnnotation.isOr())) {
      String res = "";
      try {
        final MethodSignature methodSignature = (MethodSignature) point.getSignature();
        final Method method = methodSignature.getMethod();
        res = parseKey(sysLogAnnotation.isOr(), method, point.getArgs());
      } catch (final Exception e) {
        log.error(e.getLocalizedMessage());
      }
      if ("0".equals(res)) {
        isOr = false;
      }
      if ("1".equals(res)) {
        isOr = true;
      }
    }
    if (isOr){
      try {
        final String targetName = point.getTarget().getClass().getName();
        final String methodName = point.getSignature().getName();
        final Object[] arguments = point.getArgs();
        final Class targetClass = Class.forName(targetName);
        final Method[] methods = targetClass.getMethods();
        final StringBuffer operationType = new StringBuffer();
        final StringBuffer operationName = new StringBuffer();

        for (final Method method : methods) {
          if (method.getName().equals(methodName)) {
            final Class[] clazzs = method.getParameterTypes();
            if (clazzs.length == arguments.length) {
              operationName.append(method.getName());
              for (final Object argument : arguments) {
                if (!StringUtils.isEmpty(argument)) {
                  operationType.append(argument.toString());
                }
              }
              break;
            }
          }
        }
        final long endTime = System.currentTimeMillis();
        final float excTime = (float) (endTime - startTime) / 1000;
        message = "类:" + targetName + "；方法:" + operationName + "；参数:" + operationType + "；用时:" + excTime + "秒";
      } catch (final ClassNotFoundException e) {
        log.info(e.getMessage());
      }
    }
  }

  /**
   * 执行成功。写入sysLog表
   *
   * @param res
   * @param sysLogAnnotation
   */
  @AfterReturning(returning = "res", pointcut = "sysLogAop(sysLogAnnotation)")
  public void AfterReturing(Object res, SysLogAnnotation sysLogAnnotation) {
    if (isOr){
      final HttpServletRequest request   = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
      final String             userLogin = SecurityUtils.getCurrentLoginName();
      logService.sysAddLog(sysLogAnnotation.value(), message, userLogin, request);
    }
  }

  /**
   * 执行失败。写日志 只在开发模式写日志。产品模式不记录该日志
   *
   * @param point
   * @param sysLogAnnotation
   */
  @Profile("dev")
  @AfterThrowing("sysLogAop(sysLogAnnotation)") public void afterThrowing(JoinPoint point, SysLogAnnotation sysLogAnnotation) {
    final HttpServletRequest request   = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    final String             userLogin = SecurityUtils.getCurrentLoginName();
    logService.sysAddLog(sysLogAnnotation.value(), message, userLogin, request);
    log.error("Syslog : {}，message = {}, userLogin = {}" + sysLogAnnotation.value(), message, userLogin);
  }

  private String parseKey(String key, Method method, Object[] args) {
    final LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
    final String[] paraNameArr = u.getParameterNames(method);
    final ExpressionParser parser = new SpelExpressionParser();
    final StandardEvaluationContext context = new StandardEvaluationContext();
    for (int i = 0; i < paraNameArr.length; i++) {
      context.setVariable(paraNameArr[i], args[i]);
    }
    return parser.parseExpression(key).getValue(context, String.class);
  }

}
