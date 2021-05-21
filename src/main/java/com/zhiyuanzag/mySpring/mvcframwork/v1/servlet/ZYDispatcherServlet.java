package com.zhiyuanzag.mySpring.mvcframwork.v1.servlet;

import com.sun.tools.javac.util.StringUtils;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYAutowired;
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
import java.lang.reflect.Field;
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

    //完成HandlerMapper的匹配(初始化url 和Method的一对一对应关系)
    //对添加了@ZYController的controller进行解析, 并做url和方法的映射
    private void doInitHandlerMapping() {
        if(ioc.isEmpty()) return;

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();

            if (!clazz.isAnnotationPresent(ZYController.class)){
                continue;
            }

            //1. 获取类上, 注解中定义的路径(/.../....)
            String baseUrl = "";
            if (clazz.isAnnotationPresent(ZYController.class)) {    //类上有controller注解, 需要先拼接类名上的url地址
                baseUrl += clazz.getAnnotation(ZYController.class).value();
            }
            //2. 获取类中, 各个方法的

        }
    }

    //依赖注入DI
    private void doAutowired() {
        //将类的属性中, 添加了@ZYAutoWired注解的属性进行自动赋值
        if(ioc.isEmpty()) return;

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            //利用反射, 获取到类中所有的属性
            Field[] fields = entry.getValue().getClass().getDeclaredFields();   //Declared 会取到所有的, 特定的字段, 包括private/protected/default
            for (Field f : fields) {
                if(!f.isAnnotationPresent(ZYAutowired.class)) continue;

                //确认@ZYAutowired中是否有自定义的注入vale, 有的话, 按自定义的对象名注入
                ZYAutowired autowired = f.getAnnotation(ZYAutowired.class);
                String beanName = autowired.value().trim();
                if("".equals(beanName)){
                    //无别名, 按照属性的字段名从容器中取对象
                    beanName = f.getType().getName();
                }
                //[暴力访问] 如果是public以外的修饰符, 只要加了@ZYAutowired的注解, 都要强制赋值
                f.setAccessible(true);

                try {
                    //利用反射机制, 动态给属性字段赋值
                    f.set(entry.getValue(), ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
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
                    //1. 判断@ZYService 注解中是否添加了别名
                    ZYService service = clazz.getAnnotation(ZYService.class);
                    String beanName = service.value();
                    //2. 默认首字母小写
                    if("".equals(beanName)){    //无别名
                        beanName = toLowerFirstCase(clazz.getSimpleName());
                    }
                    //3. 放入容器中
                    Object instance = clazz.newInstance();
                    ioc.put(beanName, instance);
                    //4. 根据类型自动赋值(接口与实现类)
                    for(Class<?> i :clazz.getInterfaces()){ // 接口/实现类
                        if(ioc.containsKey(i.getName())){
                            throw new Exception("The '" + i.getName() + "' is exit!");
                        }
                        ioc.put(i.getName(), instance);
                    }

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
