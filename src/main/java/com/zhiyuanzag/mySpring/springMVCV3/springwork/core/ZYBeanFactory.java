package com.zhiyuanzag.mySpring.springMVCV3.springwork.core;

/**
 * 功能描述: <br>
 * 〈spring 的核心类, 创建对象工厂的最顶层接口〉
 *
 * @author zhiyuan.zhang01
 * @param:
 * @return
 * @created 2021/6/7 19:24
*/
public interface ZYBeanFactory {

    Object getBean(String beanName);

    Object getBean(Class<?> beanCLass);
}
