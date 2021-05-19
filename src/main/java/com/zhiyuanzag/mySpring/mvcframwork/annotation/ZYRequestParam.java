package com.zhiyuanzag.mySpring.mvcframwork.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZYRequestParam {
    String value() default "";
}
