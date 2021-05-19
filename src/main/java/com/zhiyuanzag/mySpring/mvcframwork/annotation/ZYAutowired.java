package com.zhiyuanzag.mySpring.mvcframwork.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZYAutowired {
    String value() default "";
}
