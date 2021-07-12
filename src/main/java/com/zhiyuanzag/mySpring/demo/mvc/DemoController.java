package com.zhiyuanzag.mySpring.demo.mvc;

import com.zhiyuanzag.mySpring.demo.service.IDemoService;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYAutowired;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYController;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYRequestMapping;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author zhiyuanzhang9
 * @create 2021/5/22 18:22
 * @since 1.0
 */
@ZYController
@ZYRequestMapping("/demo")
public class DemoController {

    @ZYAutowired
    IDemoService iDemoService;

    @ZYRequestMapping("/show")
    public void show(HttpServletRequest req, HttpServletResponse resp, @ZYRequestParam("name") String name) {
        String result = iDemoService.show(name);
        try {
            resp.getWriter().write(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
