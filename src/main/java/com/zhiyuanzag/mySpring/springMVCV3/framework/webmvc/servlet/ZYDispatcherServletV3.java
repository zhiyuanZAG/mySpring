package com.zhiyuanzag.mySpring.springMVCV3.framework.webmvc.servlet;

import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYController;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYRequestMapping;
import com.zhiyuanzag.mySpring.springV2.framework.context.ZYApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 〈一句话功能简述〉<br>
 * 〈自定义Servlet〉
 *
 * @author zhiyuanzhang9
 * @create 2021/5/19 20:09
 * @since 1.0
 */
public class ZYDispatcherServletV3 extends HttpServlet {

    // url与对应Method的映射关系
    private Map<String, Method> handlerMapping = new HashMap<>();

    //handlerMapping 的缓存
    private List<ZYHandlerMapping> handlerMappings = new ArrayList<>();

    //handlerMapping与handlerAdapter的映射表
    private Map<ZYHandlerMapping, ZYHandlerAdapter> handlerAdapters = new HashMap<>();

    //ViewResolver的缓存
    private ZYViewResolver viewResolver;


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
        //1. 根据url取得handlerMapping
        ZYHandlerMapping mapping = this.getHandlerMapping(req);

        //2. 根据HandlerMapping, 取得对应的handlerAdapter
        if (null == mapping) {
            processDispatchResult(req, resp, new ZYModelAndView("404"));
            return;
        }
        ZYHandlerAdapter ha = this.getHandlerAdapter(mapping);

        //3. 执行adapter的handler方法, 处理对应的请求, 返回ModelAndView
        ZYModelAndView mv = ha.handler(req, resp, mapping);

        //4. 根据ViewResolver找到对应的view对象, 通过view对象渲染页面并返回结果
        processDispatchResult(req, resp, mv);

    }


    @Override
    public void init(ServletConfig config) throws ServletException {

        context = new ZYApplicationContext(config.getInitParameter("contextConfigLocation"));

        //========= mvc功能 ==========
        //容器启动的初始化策略
        initStrategies(context);

        System.out.println("============= ZY Spring framework is init. =============");

    }


    /*********** 私有方法 ***********/

    /**
     * 功能描述: <br>
     * 〈根据handlerMapping, 从容器中筛选出匹配的handlerAdapter〉
     *
     * @author zhiyuan.zhang01
     * @param: [mapping]
     * @return com.zhiyuanzag.mySpring.springMVCV3.framework.webmvc.servlet.ZYHandlerAdapter
     * @created 2021/7/12 15:17
    */
    private ZYHandlerAdapter getHandlerAdapter(ZYHandlerMapping mapping) {
        return this.handlerAdapters.entrySet().stream().filter(entry -> entry.getKey().equals(mapping)).findFirst().get().getValue();
    }

    /**
     * 功能描述: <br>
     * 〈根据request, 筛选出容器中匹配的handlerMapping〉
     *
     * @author zhiyuan.zhang01
     * @param: [req]
     * @return com.zhiyuanzag.mySpring.springMVCV3.framework.webmvc.servlet.ZYHandlerMapping
     * @created 2021/7/12 15:16
    */
    private ZYHandlerMapping getHandlerMapping(HttpServletRequest req) {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();  //获取到的是服务名(如: http://localhost:8080/spring-mvc/start/query, 则获取到是/spring-mvc)
        final String fUrl = url.replaceAll(contextPath, "")   //去除掉服务名
                .replaceAll("/+", "/"); //将多个'//....' 替换成'/'

        return handlerMappings.stream().filter(mapping-> mapping.getPattern().matcher(fUrl).matches()).findFirst().orElse(null);
    }

    /**
     * 功能描述: <br>
     * 〈根据handler处理后返回的ModelAndViewView, 组装返回视图〉
     *
     * @author zhiyuan.zhang01
     * @param: [req, resp, zyModelAndView]
     * @return void
     * @created 2021/7/12 15:22
    */
    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, ZYModelAndView zyModelAndView) throws IOException {
        //根据handler处理后返回的ModelAndView, 查找到对应View, 在调用其对应的render()方法处理mav中的model
        if(zyModelAndView == null) return;;
        ZYView view = this.viewResolver.resolveViewName(zyModelAndView.getViewName());
        view.render(zyModelAndView.getModel(), req, resp);
    }

    /**
     * 功能描述: <br>
     * 〈SpringMVC启动的初始化策略〉
     *
     * @author zhiyuan.zhang01
     * @param: [context]
     * @return void
     * @created 2021/7/7 19:58
    */
    private void initStrategies(ZYApplicationContext context) {
        //handlerMapping
        doInitHandlerMapping(context);
        //初始化参数适配器
        doInitHandlerAdapters(context);
        //初始化图形转换器
        doInitViewResolvers(context);
    }






    /**
     * 功能描述: <br>
     * 〈初始化图形转换器〉
     *
     * @author zhiyuan.zhang01
     * @param: [context]
     * @return void
     * @created 2021/7/7 20:06
    */
    private void doInitViewResolvers(ZYApplicationContext context) {
        // 2021/7/7 将系统中定义的各种view模板加载进容器(404.html ...)
        String templateRoot = context.getConfig().getProperty("templateRoot");
        this.viewResolver = new ZYViewResolver(templateRoot);
    }

    /**
     * 功能描述: <br>
     * 〈初始化参数适配器〉
     *
     * @author zhiyuan.zhang01
     * @param: [context]
     * @return void
     * @created 2021/7/7 20:05
    */
    private void doInitHandlerAdapters(ZYApplicationContext context) {
        //2021/7/7 给系统的handlerMapping匹配对应的adapter
        this.handlerMappings.forEach(mapping-> this.handlerAdapters.put(mapping, new ZYHandlerAdapter()));
    }

    //v3: 对handlerMapping进行封装
    /**
     * 功能描述: <br>
     * 〈handlerMapping适配;
     * HandlerMapper的匹配(初始化url 和Method的一对一对应关系)〉
     *
     * @author zhiyuan.zhang01
     * @param: [context]
     * @return void
     * @created 2021/7/7 20:05
    */
    private void doInitHandlerMapping(ZYApplicationContext context) {
        if(context.getBeanDefinitionCount() == 0) return;

        for (String beanName : context.getBeanDefinitionNames()) {

            Object instance = context.getBean(beanName);
            Class<?> clazz = instance.getClass();

            if (!clazz.isAnnotationPresent(ZYController.class)){    //对添加了@ZYController的controller进行解析, 并做url和方法的映射
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

                // //demo//query
                String regex = ("/" + baseUrl + "/" +requestMapping.value())
                        .replaceAll("\\*", ".*")    //
                        .replaceAll("/+", "/"); //将多余的"//" 转化为"/"
                Pattern pattern = Pattern.compile(regex);

                handlerMappings.add(new ZYHandlerMapping(instance, method, pattern));
                System.out.println("Mapped : " + regex + " --> " + method);
            }
        }
    }




}
