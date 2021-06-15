package com.zhiyuanzag.mySpring.springMVCV3.springwork.beans.config;

/**
 * 〈一句话功能简述〉<br>
 * 〈被spring管理的bean定义类〉
 *
 * @author zhiyuanzhang9
 * @create 2021/6/7 19:27
 * @since 1.0
 */
public class ZYBeanDefinition {

    //是否延迟加载(此处写死不延迟)
    public boolean isLazyInit(){
        return false;
    }

    private String factoryBeanName; //被工厂管理的map中的key-> beanName
    private String beanClassName;   //原生类的全类名


    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
}