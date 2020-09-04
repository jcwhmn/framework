package com.yangzhou.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.yangzhou.enums.EventType;

/**
 * 订阅事件，在需要订阅消息的方法上注解。由EventBus调用。
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
  // domain类名，如Role, Person等。也是BaseEntityService中的businessType
  String type();

  // 事件类型：Insert, Update, Delete
  EventType method();
}
