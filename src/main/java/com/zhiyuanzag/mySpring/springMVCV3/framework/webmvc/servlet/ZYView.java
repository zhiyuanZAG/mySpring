package com.zhiyuanzag.mySpring.springMVCV3.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈http请求的返回视图〉
 *
 * @author zhiyuanzhang9
 * @create 2021/7/8 20:36
 * @since 1.0
 */
public class ZYView {

    //返回的具体文件
    private File view;

    public ZYView(File view) {
        this.view = view;
    }
    
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response){
        // TODO: 2021/7/8 待实现 
    }
}
