package com.zhiyuanzag.mySpring.springV2.framework.beans.support;

import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYAutowired;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYController;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYService;
import com.zhiyuanzag.mySpring.springV2.framework.beans.ZYBeanWrapper;
import com.zhiyuanzag.mySpring.springV2.framework.beans.config.ZYBeanDefinition;
import com.zhiyuanzag.mySpring.springV2.framework.core.ZYBeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈对bean工厂类的实现, 对某一个维度进行细化与扩展<br>
 * (此处的ListableBeanFactory中会储存所有的Bean对象, 但是没有层级结构, 也可以进行其他实现方式的扩展, 例如扩展方向为按照类的层级结构储存bean对象)〉
 *
 * @author zhiyuanzhang9
 * @create 2021/6/7 19:34
 * @since 1.0
 */
public class ZYDefaultListableBeanFactory implements ZYBeanFactory {

    //map中存的实际是容器的beanName-> beanDefinition的映射关系
    public Map<String, ZYBeanDefinition> beanDefinitionMap = new HashMap<>();

    //三级缓存(终极缓存)
    public Map<String, ZYBeanWrapper> factoryBeanWrapperCache = new HashMap<>();

    //实体对象的缓存
    public Map<String, Object> factoryObjectCache = new HashMap<>();

    @Override
    public Object getBean(String beanName) {
        return this.factoryObjectCache.get(beanName);
    }

    @Override
    public Object getBean(Class<?> beanCLass) {
        return this.getBean(beanCLass.getName());
    }

    /**
     * 功能描述: <br>
     * 〈将从各处加载/扫描到的类定义Definition, 注册到容器map中, 用于后续创建实例〉
     *
     * @author zhiyuan.zhang01
     * @param: [beanDefinitions]
     * @return void
     * @created 2021/6/7 20:18
    */
    public void doRegistryBeanDefinition(List<ZYBeanDefinition> beanDefinitions) throws Exception{
        for (ZYBeanDefinition beanDefinition : beanDefinitions) {
            if (this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The " + beanDefinition.getFactoryBeanName() + " is exits!!");
            }
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    public void doLoadInstance() {
        //循环调用doCreateBean方法
        for (Map.Entry<String, ZYBeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            if (!entry.getValue().isLazyInit()) {   //非延迟加载的对象
                doCreateBean(beanName);
            }
        }
    }



    // ========= 私有方法 ==========
    /**
     * 功能描述: <br>
     * 〈构建三级缓存, 并实际创建对象〉
     *
     * @author zhiyuan.zhang01
     * @param: [beanName]
     * @return void
     * @created 2021/6/7 20:48
    */
    private void doCreateBean(String beanName) {
        //1. 根据beanName, 从beanDefinitionMap中取得BeanDefinition配置信息
        ZYBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        //2. 使用反射实例化对象
        Object instance = instaniateBean(beanName, beanDefinition);

        //3. 将返回的bean封装成wrapper
        ZYBeanWrapper beanWrapper = new ZYBeanWrapper(instance);

        //4. 执行依赖注入
        populateBean(beanName, beanDefinition, beanWrapper);

        //5. 保存到IoC的三级缓存容器中
        this.factoryBeanWrapperCache.put(beanName, beanWrapper);
    }

    //对bean对象的属性进行依赖注入
    private void populateBean(String beanName, ZYBeanDefinition beanDefinition, ZYBeanWrapper beanWrapper) {
        //将类的属性中, 添加了@ZYAutoWired注解的属性进行自动赋值
        //只单独注入一个对象的依赖
        Class<?> beanClass = beanWrapper.getWrapperClass();

        Object instance = beanWrapper.getWapperInstance();

        if (!(beanClass.isAnnotationPresent(ZYController.class) || beanClass.isAnnotationPresent(ZYService.class))) {
            return;
        }

        //忽略字段的修饰符, 不管是private/ protected/ public/ default
        for (Field f : beanClass.getDeclaredFields()) {
            if (!f.isAnnotationPresent(ZYAutowired.class)) {
                continue;
            }

            //取 autowired 自定义的别名
            ZYAutowired autowired = f.getAnnotation(ZYAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = f.getType().getName();
            }

            //强制访问
            f.setAccessible(true);

            try {
                if (this.factoryObjectCache.get(autowiredBeanName) == null) {
                    continue;
                }
                //todo 此处设置自动注入时, 是如何保证容器中实例化顺序的问题的?
                //todo 如果存在循环依赖, 要怎么处理?
                f.set(instance, this.factoryBeanWrapperCache.get(autowiredBeanName).getWapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //使用反射实例化对象
    private Object instaniateBean(String beanName, ZYBeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {
            Class<?> clazz = Class.forName(className);

            instance = clazz.newInstance();

            this.factoryObjectCache.put(beanName, instance);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }


}
