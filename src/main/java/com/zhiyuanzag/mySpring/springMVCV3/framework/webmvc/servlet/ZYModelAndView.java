package com.zhiyuanzag.mySpring.springMVCV3.framework.webmvc.servlet;

import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈http请求的返回modelAndView〉
 *
 * @author zhiyuanzhang9
 * @create 2021/7/8 20:31
 * @since 1.0
 */
public class ZYModelAndView {

    //视图名称
    private String viewName;

    //视图名与对应View的映射关系
    private Map<String, ?> model;

    public ZYModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public ZYModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}
