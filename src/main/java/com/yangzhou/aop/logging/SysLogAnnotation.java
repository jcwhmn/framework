package com.yangzhou.aop.logging;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLogAnnotation {
    String value() default "系统日志";
    String isOr() default "";
    // 当isOr不为空就会去解析 传进来的 实体.参数
    // 通过解析得到0或者1 [0为默认 1为不执行日志记录]
}