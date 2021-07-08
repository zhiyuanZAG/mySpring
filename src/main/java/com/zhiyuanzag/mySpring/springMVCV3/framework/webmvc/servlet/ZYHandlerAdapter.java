package com.zhiyuanzag.mySpring.springMVCV3.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 〈一句话功能简述〉<br>
 * 〈Handler的适配器〉
 *
 * @author zhiyuanzhang9
 * @create 2021/7/7 20:17
 * @since 1.0
 */
public class ZYHandlerAdapter {


    /**
     * 功能描述: <br>
     * 〈通过传参中的handler, 处理httpRequest, 写入HTTPResponse, 并返回ModelAndView〉
     *
     * @author zhiyuan.zhang01
     * @param: [request, response, handler]
     * @return com.zhiyuanzag.mySpring.springMVCV3.framework.webmvc.servlet.ZYModelAndView
     * @created 2021/7/8 20:32
    */
    public ZYModelAndView handler(HttpServletRequest request, HttpServletResponse response, ZYHandlerMapping handler){
// TODO: 2021/7/8 待实现 
        return null;
    }
}
