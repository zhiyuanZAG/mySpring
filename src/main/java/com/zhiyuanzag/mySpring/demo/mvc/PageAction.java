package com.zhiyuanzag.mySpring.demo.mvc;

import com.zhiyuanzag.mySpring.demo.service.QueryService;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYAutowired;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYController;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYRequestMapping;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYRequestParam;
import com.zhiyuanzag.mySpring.springMVCV3.framework.webmvc.servlet.ZYModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈直接返回页面ModelAndView〉
 *
 * @author zhiyuanzhang9
 * @create 2021/7/12 16:57
 * @since 1.0
 */
@ZYController
@ZYRequestMapping("/")
public class PageAction {


    @ZYAutowired
    private QueryService queryService;

    @ZYRequestMapping("/first.html")
    public ZYModelAndView queryTeacher(@ZYRequestParam("teacher") String teacher) {
        String result = queryService.query(teacher);
        Map<String, Object> model = new HashMap<>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");

        return new ZYModelAndView("first.html", model);
    }
}
