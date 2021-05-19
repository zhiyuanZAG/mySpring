package com.zhiyuanzag.mySpring.mvcframwork.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZYRequestMapping {

}
