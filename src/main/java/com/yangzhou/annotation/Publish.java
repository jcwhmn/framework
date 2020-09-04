package com.yangzhou.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import com.yangzhou.enums.EventType;

/**
 * 发布对象注解，domain实体上注解。缺省对象的增删改操作都需要发布。
 * 若只是发布增删改操作的部分时，可以使用methods来指定发布的操作类型。
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Publish {
  @AliasFor("methods") EventType[] value() default EventType.All;

  @AliasFor("value")
  EventType[] methods() default EventType.All;
}
