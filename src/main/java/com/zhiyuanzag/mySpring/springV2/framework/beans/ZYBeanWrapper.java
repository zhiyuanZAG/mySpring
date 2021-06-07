package com.zhiyuanzag.mySpring.springV2.framework.beans;

/**
 * 〈一句话功能简述〉<br>
 * 〈bean对象的包装类, 实际在spring中, 也是管理并操作其代理对象执行逻辑〉
 *
 * @author zhiyuanzhang9
 * @create 2021/6/7 19:39
 * @since 1.0
 */
public class ZYBeanWrapper {

    private Object wapperInstance;
    private Class<?> wrapperClass;

    public ZYBeanWrapper(Object wapperInstance, Class<?> wrapperClass) {
        this.wapperInstance = wapperInstance;
        this.wrapperClass = wrapperClass;
    }

    public ZYBeanWrapper(Object wapperInstance) {
        this.wapperInstance = wapperInstance;
    }

    public Object getWapperInstance() {
        return wapperInstance;
    }

    public void setWapperInstance(Object wapperInstance) {
        this.wapperInstance = wapperInstance;
    }

    public Class<?> getWrapperClass() {
        return wrapperClass;
    }

    public void setWrapperClass(Class<?> wrapperClass) {
        this.wrapperClass = wrapperClass;
    }
}
