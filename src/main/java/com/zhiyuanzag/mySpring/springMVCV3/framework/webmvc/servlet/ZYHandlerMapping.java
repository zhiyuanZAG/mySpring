package com.zhiyuanzag.mySpring.springMVCV3.framework.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * 〈一句话功能简述〉<br>
 * 〈HandlerMapping 中记录url与指定controller的指定method的绑定关系〉
 *
 * @author zhiyuanzhang9
 * @create 2021/7/7 20:13
 * @since 1.0
 */
public class ZYHandlerMapping {

    //对应的controller
    private Object controller;

    //url匹配出来的method
    private Method method;

    //url匹配
    private Pattern pattern;

    public ZYHandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
