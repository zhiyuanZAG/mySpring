package com.zhiyuanzag.mySpring.springV2.framework.webmvc.servlet;

import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYRequestMapping;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYRequestParam;
import com.zhiyuanzag.mySpring.springV2.framework.context.ZYApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈自定义Servlet〉
 *
 * @author zhiyuanzhang9
 * @create 2021/5/19 20:09
 * @since 1.0
 */
public class ZYDispatcherServletV2 extends HttpServlet {

    // url与对应Method的映射关系
    private Map<String, Method> handlerMapping = new HashMap<>();

    // 容器上下文
    private ZYApplicationContext context;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //重写doGet()方法
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //实际的运行阶段
        try {
            doDispatcher(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Exception, Detail : "+ Arrays.toString(e.getStackTrace()));
        }
    }

    //doPost的实际运行
    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {
        //取出当前请求的url, 从容器中取出对应的方法, 然后invoke执行
        String url = req.getRequestURI();
        String contxetPath = req.getContextPath();
        url = url.replaceAll(contxetPath, "").replaceAll("/", "/");
        if (!this.handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 Not Found!");
            return;
        }
        Map<String, String[]> params = req.getParameterMap();   //请求中的参数map
        Method method = handlerMapping.get(url);

        //获取形参列表, 并添加对@ZYRequestParam的解析
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] paramValues = new Object[paramTypes.length];

        //遍历方法的形参列表
        for (int i = 0; i < paramTypes.length; i++) {
            Class paramType = paramTypes[i];
            if (paramType == HttpServletRequest.class) {
                paramValues[i] = req;
            } else if (paramType == HttpServletResponse.class) {
                paramValues[i] = resp;
            } else if (paramType == String.class) {

                //通过运行时状态去拿到
                Annotation[][] pa = method.getParameterAnnotations();   //参数的注解, 是一个二维数组
                for (int j = 0; j < pa.length; j++) {
                    for (Annotation a : pa[j]) {
                        if (a instanceof ZYRequestParam) {  //参数注解为@ZYRequestParam
                            String paramName = ((ZYRequestParam) a).value();    //注解的别名

                            if (!"".equals(paramName)) {    //参数有注解的别名
                                String value = Arrays.toString(params.get(paramName))
                                        .replaceAll("\\[|\\]", "")  //替换方括号
                                        .replaceAll("\\s+", ",");   //匹配替换空白字符, 包括空格, 制表符, 换页符等
                                paramValues[i] = value;
                            }else{  //参数无注解别名, 使用形参名, 作为参数映射的key
                                // 注意此处: 必须在java8以上版本, 在编译时, 添加-parameters编译命令, 才能在.class文件中, 正确的将原形参名编译进去. 利用反射在容器中取到对应的实参.
                                Parameter[] parameters = method.getParameters();
                                String paraName = parameters[i].getName();
                                String value = Arrays.toString(params.get(paraName))
                                        .replaceAll("\\[|\\]", "")  //替换方括号
                                        .replaceAll("\\s+", ",");   //匹配替换空白字符, 包括空格, 制表符, 换页符等
                                paramValues[i] = value;

                            }
                        }
                    }
                }
            }
        }

        //获取对应的对象, 利用反射, 执行该方法
        String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        method.invoke(context.getBean(beanName), paramValues);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        context = new ZYApplicationContext(config.getInitParameter("contextConfigLocation"));
        //========= mvc功能 ==========
        //5. 初始化HandlerMapping(将url与method匹配)
        doInitHandlerMapping();

        System.out.println("============= ZY Spring framework is init. =============");

    }


    /*********** 私有方法 ***********/

    //完成HandlerMapper的匹配(初始化url 和Method的一对一对应关系)
    //对添加了@ZYController的controller进行解析, 并做url和方法的映射
    private void doInitHandlerMapping() {
        if(context.getBeanDefinitionCount() == 0) return;

        for (String beanName : context.getBeanDefinitionNames()) {

            Object instance = context.getBean(beanName);
            Class<?> clazz = instance.getClass();

            if (!clazz.isAnnotationPresent(ZYRequestMapping.class)){
                continue;
            }

            //1. 获取类上, 注解中定义的路径(/.../....)
            String baseUrl = "";
            if (clazz.isAnnotationPresent(ZYRequestMapping.class)) {    //类上有controller注解, 需要先拼接类名上的url地址
                baseUrl += clazz.getAnnotation(ZYRequestMapping.class).value();
            }
            //2. 获取各个类中, 各个方法上的@ZYController注解
            for (Method method : clazz.getMethods()) {  //只迭代public方法
                if (!method.isAnnotationPresent(ZYRequestMapping.class)) {
                    continue;
                }
                ZYRequestMapping requestMapping = method.getAnnotation(ZYRequestMapping.class);
                String url = ("/" + baseUrl + "/" +requestMapping.value())
                        .replaceAll("/+", "/"); //将多余的"//" 转化为"/"
                handlerMapping.put(url, method);
                System.out.println("Mapped : " + url + " --> " + method);
            }
        }
    }

    //将首字母变更为小写
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

}
