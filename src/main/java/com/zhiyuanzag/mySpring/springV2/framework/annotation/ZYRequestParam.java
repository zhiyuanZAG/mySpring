package com.zhiyuanzag.mySpring.springV2.framework.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZYRequestParam {
    String value() default "";
}
