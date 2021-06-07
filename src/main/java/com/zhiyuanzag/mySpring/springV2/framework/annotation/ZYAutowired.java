package com.zhiyuanzag.mySpring.springV2.framework.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZYAutowired {
    String value() default "";
}
