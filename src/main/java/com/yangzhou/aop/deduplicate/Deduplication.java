package com.yangzhou.aop.deduplicate;


import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Deduplication {

    String value();
}
