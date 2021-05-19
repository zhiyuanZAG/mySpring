package com.zhiyuanzag.mySpring.mvcframwork.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZYService {
    String value() default "";
}
