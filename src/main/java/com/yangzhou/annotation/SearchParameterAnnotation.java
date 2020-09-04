package com.yangzhou.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchParameterAnnotation {
  @AliasFor("columnName")
  String value() default "";
  
  @AliasFor("value")
  String columnName() default "";
  
  String type() default "eq";
  boolean ignore() default false;
}
