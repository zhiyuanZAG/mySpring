package com.zhiyuanzag.mySpring.springMVCV3.framework.webmvc.servlet;

import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

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
    public ZYModelAndView handler(HttpServletRequest req, HttpServletResponse resp, ZYHandlerMapping handler) throws InvocationTargetException, IllegalAccessException {
        // 解析req的参数列表, 根据传参handler定义的controller以及method, 直接调用method.invoke()方法, 执行对应的方法
        Map<String, String[]> params = req.getParameterMap();   //请求中的参数map
        Method method = handler.getMethod();

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
        Object result = method.invoke(handler.getController(), paramValues);
        if (result == null || result instanceof Void) {
            return null;
        }

        boolean isModelAndView = handler.getMethod().getReturnType() == ZYModelAndView.class;
        if (isModelAndView) {
            return (ZYModelAndView) result;
        }
        return null;
    }


    //将首字母变更为小写
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
