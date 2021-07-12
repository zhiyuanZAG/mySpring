package com.zhiyuanzag.mySpring.springV2.framework.context;

import com.zhiyuanzag.mySpring.springV2.framework.beans.config.ZYBeanDefinition;
import com.zhiyuanzag.mySpring.springV2.framework.beans.support.ZYBeanDefinitionReader;
import com.zhiyuanzag.mySpring.springV2.framework.beans.support.ZYDefaultListableBeanFactory;
import com.zhiyuanzag.mySpring.springV2.framework.core.ZYBeanFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 〈一句话功能简述〉<br>
 * 〈spring 容器的上下文, 定义了BeanFactory属性, 可以直接从context获取到容器注册了的所有对象〉
 *
 * @author zhiyuanzhang9
 * @create 2021/6/7 19:26
 * @since 1.0
 */
public class ZYApplicationContext implements ZYBeanFactory {

    private ZYDefaultListableBeanFactory registry = new ZYDefaultListableBeanFactory();

    private ZYBeanDefinitionReader reader;

    //构造方法
    public ZYApplicationContext(String... configLocations) {
        //1. 加载配置文件
        reader = new ZYBeanDefinitionReader(configLocations);
        try {
            //2. 解析配置文件, 将所有的配置文件封装成BeanDefinition对象
            List<ZYBeanDefinition> list = reader.loadBeanDefinitions();

            //3. 将所有的配置信息缓存起来
            registry.doRegistryBeanDefinition(list);
            //4. 加载所有的非延时加载的bean
            registry.doLoadInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Object getBean(String beanName) {
        return registry.getBean(beanName);
    }

    @Override
    public Object getBean(Class<?> beanCLass) {
        return registry.getBean(beanCLass);
    }

    public int getBeanDefinitionCount() {
        return registry.beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames() {
        return this.registry.beanDefinitionMap.keySet().toArray(new String[0]);
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }
}
