package com.zhiyuanzag.mySpring.springV2.framework.beans.support;

import com.zhiyuanzag.mySpring.springV2.framework.beans.config.ZYBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 〈一句话功能简述〉<br>
 * 〈bean对象的获取类<br>
 *     职责: 1. 加载spring的properties, 进行包扫描等操作
 *           2. 提供方法, 将扫描到的对象封装成BeanDefinition, 并提供出去〉
 *
 * @author zhiyuanzhang9
 * @create 2021/6/7 19:31
 * @since 1.0
 */
public class ZYBeanDefinitionReader {

    //保存用户配置好的配置文件
    private Properties configContext = new Properties();

    //缓存从包路径下扫描的全类名, 需要被注册的beanClass们的全类名
    private List<String> registryBeanClass = new ArrayList<>();

    /**
     * 功能描述: <br>
     * 〈构造方法: 直接加载配置文件+包扫描〉
     *
     * @author zhiyuan.zhang01
     * @param: [locations]
     * @return
     * @created 2021/6/7 19:52
    */
    public ZYBeanDefinitionReader(String... locations) {
        //1. 加载properties文件
        doLoadConfig(locations[0]); //默认只读取传进来的第一个配置文件的路径

        //2. 进行包扫描
        doScanner(configContext.getProperty("scanPackage"));
    }


    /**
     * 功能描述: <br>
     * 〈对外提供方法, 将包扫描到的所有类, 包装成BeanDefinition对象返回〉
     *
     * @author zhiyuan.zhang01
     * @param: []
     * @return java.util.List<com.zhiyuanzag.mySpring.springV2.framework.beans.config.ZYBeanDefinition>
     * @created 2021/6/7 20:02
    */
    public List<ZYBeanDefinition> loadBeanDefinitions() {
        List<ZYBeanDefinition> result = new ArrayList<>();

        //遍历已注册的全类名, 依次封装成ZYBeanDefinition对象
        for (String className : registryBeanClass) {
            try {
                Class<?> beanClazz = Class.forName(className);
                //如果beanCLass本身是接口, 就不做处理
                if (beanClazz.isInterface()) {  // (接口的映射处理, 通过获取类的所有实现接口, 来进行接口的绑定, 此处如果遍历到的clazz本身是接口, 就不做处理了, 否则可能会重复创建接口的definition)
                    continue;
                }
                //1. 默认类名首字母小写的情况
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClazz.getSimpleName()), beanClazz.getName()));
                //2. 处理该类的所有实现接口(在spring的依赖注入DI中, 可以定义接口直接注入, 即在容器中, 接口的类定义(key)与实现类的类定义(key)所对应的类对象(value)是同一个)
                for (Class<?> clazz : beanClazz.getInterfaces()) {
                    result.add(doCreateBeanDefinition(clazz.getName(), beanClazz.getName()));   //beanDefinition中, 以接口名clazz.getName()作为key, 以实现类的全类名beanClazz.getName()作为value
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /***** 以下为私有方法 ****/
    //创建beanDefinition对象
    private ZYBeanDefinition doCreateBeanDefinition(String factoryBeanName, String factoryClassName) {
        ZYBeanDefinition definition = new ZYBeanDefinition();
        definition.setFactoryBeanName(factoryBeanName); //对象名
        definition.setBeanClassName(factoryClassName);  //全类名
        return definition;
    }

    //包扫描
    private void doScanner(String scanPackage) {
        //加载配置文件
        //从当前类路径下找到Spring配置文件所在的路径
        //并且将读取出来的配置放到Properties中
        //即, 将application.properties中的配置转移到了内存中(scanPackage = com.zhiyuanzag.mySpring.demo)
        InputStream fil = this.getClass().getClassLoader().getResourceAsStream(scanPackage);
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

    //加载配置文件
    private void doLoadConfig(String scanPackage) {
        //包扫描
        URL url = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getPath());
        for(File file : classPath.listFiles()){
            if(file.isDirectory()){ //文件夹
                doScanner(scanPackage + "." + file.getName());
            }else { //文件
                if(!file.getName().endsWith(".class")) continue;    //只有class文件才会加载到类名集合中
                String className = scanPackage + "." + file.getName().replaceAll(".class", "");
                registryBeanClass.add(className);
            }
        }
    }


    //工具方法: 将首字母变更为小写
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
