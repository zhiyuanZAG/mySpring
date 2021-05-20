package com.zhiyuanzag.mySpring.mvcframwork.v1.servlet;

import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYController;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈自定义Servlet〉
 *
 * @author zhiyuanzhang9
 * @create 2021/5/19 20:09
 * @since 1.0
 */
public class ZYDispatcherServlet extends HttpServlet {

    //配置文件内容上下文
    private Properties configContext = new Properties();

    //全类名集合
    private List<String> classNames = new ArrayList<>();

    //servlet容器
    private Map<String, Object> ioc = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //重写doGet()方法
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //实际的运行阶段
        doDispatcher(req, resp);
    }

    //doPost的实际运行
    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) {
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1. 加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //2. 包扫描 + 扫描相关的类
        doScanner(configContext.getProperty("scanPackage"));

        //3. 初始化IOC容器
        doInstance();

        //4. 完成DI注入
        doAutowired();

        //5. 初始化HandlerMapping(将url与method匹配)
        doInitHandlerMapping();

        System.out.println("ZY Spring framework is init.");

    }


    /*********** 私有方法 ***********/

    //完成HandlerMapper的匹配
    private void doInitHandlerMapping() {
    }

    //依赖注入DI
    private void doAutowired() {
    }

    //IoC容器实例化(使用反射, 创建)
    private void doInstance() {
        //初始化, 为DI做准备
        if(this.classNames.isEmpty()) return;

        //根据类名, 通过反射创建类的实体, 并放到IoC容器中
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);  //当前类的class

                //什么样的类才需要进行初始化? -> 添加了注解的类, 才进行初始化
                //样例中只列举@contorller 和@Service
                if(clazz.isAnnotationPresent(ZYController.class)){  //@ZYController 注解
                    Object instance = clazz.newInstance();   //创建实例
                    String beanName = toLowerFirstCase(clazz.getSimpleName());  //实例的映射key名 (需要将类名的首字母小写)
                    ioc.put(beanName, instance);
                }else if(clazz.isAnnotationPresent(ZYService.class)){   //@ZYService 注解
                    //需要判断@ZYService 注解中是否添加了别名


                }else{
                    //.... 对其他种类注解的处理
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //将首字母变更为小写
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    //包扫描
    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getPath());
        for(File file : classPath.listFiles()){
            if(file.isDirectory()){ //文件夹
                doScanner(scanPackage + "." + file.getName());
            }else { //文件
                if(!file.getName().endsWith(".class")) continue;    //只有class文件才会加载到类名集合中
                String className = scanPackage + "." + file.getName().replaceAll(".class", "");
                classNames.add(className);
            }
        }
    }

    //加载配置文件
    private void doLoadConfig(String contextConfigLocation) {
        //从当前类路径下找到Spring配置文件所在的路径
        //并且将读取出来的配置放到Properties中
        //即, 将application.properties中的配置转移到了内存中(scanPackage = com.zhiyuanzag.mySpring.demo)
        InputStream fil = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            this.configContext.load(fil);   //配置文件加载到Properties中
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭流
            try {
                fil.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
