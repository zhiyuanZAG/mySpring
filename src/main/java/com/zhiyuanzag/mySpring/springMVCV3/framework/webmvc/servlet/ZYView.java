package com.zhiyuanzag.mySpring.springMVCV3.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
    /**
     * 功能描述: <br>
     * 〈读取系统内定义的各个view(404.html .....), 写入到resp〉
     *
     * @author zhiyuan.zhang01
     * @param: [model, request, response]
     * @return void
     * @created 2021/7/12 16:10
    */
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 2021/7/8 读取系统内定义的模板(404.html, 500.html/....), 并替换模板中可能存在的占位符
        StringBuilder sb = new StringBuilder();
        RandomAccessFile ra = new RandomAccessFile(this.view, "r");

        String line = null;
        while (null != (line = ra.readLine())) {
            line = new String(line.getBytes("iso-8859-1"), "utf-8");

            Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                //如first.html中的￥{teacher}
                String paramName = matcher.group();

                paramName = paramName.replaceAll("￥\\{|\\}", "");   //将占位符替换掉
                Object paramValue = model.get(paramName);
                if ((null == paramName)) {
                    continue;
                }
                line = matcher.replaceFirst(makingStringForRegExp(paramValue.toString()));
                matcher = pattern.matcher(line);
            }
            sb.append(line);
        }

        response.setCharacterEncoding("utf-8");
        response.getWriter().write(sb.toString());

    }

    //处理特殊字符
    private String makingStringForRegExp(String str) {
        return str.replace("\\", "\\\\")
                .replace("*", "\\*")
                .replace("+", "\\+")
                .replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
