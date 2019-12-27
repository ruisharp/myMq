package com.my.mq.commom.demo.util;

import java.util.Map;
import org.springframework.context.ApplicationContext;

/**
 * Spring容器工具，用于在非上下文环境下获取spring容器内的信息
 *
 * @author xuchaoguo
 */
public final class SpringBeanUtil {
    /**
     * Spring上下文实例，通过SpringContextAware初始化
     */
    private static ApplicationContext APP_CONTEXT;

    private SpringBeanUtil() {
    }

    public static void setAppContext(ApplicationContext springContext) {
        APP_CONTEXT = springContext;
    }

    /**
     * 获取应用上下文
     *
     * @return 应用上下文
     */
    public static ApplicationContext getAppContext() {
        return APP_CONTEXT;
    }

    /**
     * 通过bean的ID获取bean的实例
     *
     * @param beanId bean的ID标示
     * @return bean实例
     */
    public static Object getBean(String beanId) {
        return APP_CONTEXT.getBean(beanId);
    }

    /**
     * 通过bean的类型获取实例
     *
     * @param clazz 类型
     * @param <T>   范型定义
     * @return bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return APP_CONTEXT.getBean(clazz);
    }

    /**
     * 通过bean的名称和类型获取实例
     *
     * @param beanName bean的名称
     * @param clazz    类型
     * @param <T>      范型定义
     * @return bean实例
     */
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return APP_CONTEXT.getBean(beanName, clazz);
    }

    /**
     * 获取某一类的所有的bean
     *
     * @param clazz 类型
     * @param <T>   范型定义
     * @return bean名称和实例的map对象
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return APP_CONTEXT.getBeansOfType(clazz);
    }

    /**
     * 获取某个属性参数值
     *
     * @param name 属性名
     * @return 属性值
     */
    public static String getProperty(String name) {
        return APP_CONTEXT.getEnvironment().getProperty(name);
    }
}
