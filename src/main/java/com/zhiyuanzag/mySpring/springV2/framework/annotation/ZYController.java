package com.zhiyuanzag.mySpring.springV2.framework.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZYController {
    String value() default "";
}
