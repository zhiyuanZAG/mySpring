package com.zhiyuanzag.mySpring.springV2.framework.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZYRequestMapping {
    String value() default "";
}
