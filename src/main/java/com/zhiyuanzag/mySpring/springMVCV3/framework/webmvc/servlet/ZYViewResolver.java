package com.zhiyuanzag.mySpring.springMVCV3.framework.webmvc.servlet;

import java.io.File;
import java.util.Objects;

/**
 * 〈一句话功能简述〉<br>
 * 〈View的处理器(创建新的View对象)〉
 *
 * @author zhiyuanzhang9
 * @create 2021/7/8 20:42
 * @since 1.0
 */
public class ZYViewResolver {

    //view的文件后缀 .vm, .flt, .jsp ...
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    private File templateRootDir;

    //构造器
    public ZYViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    /**
     * 功能描述: <br>
     * 〈根据viewName, 转化生成View对象〉
     *
     * @author zhiyuan.zhang01
     * @param: [viewName]
     * @return com.zhiyuanzag.mySpring.springMVCV3.framework.webmvc.servlet.ZYView
     * @created 2021/7/8 20:46
    */
    public ZYView resolveViewName(String viewName) {
        if(null == viewName || "".equals(viewName.trim())){
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));

        return new ZYView(templateFile);
    }
}
