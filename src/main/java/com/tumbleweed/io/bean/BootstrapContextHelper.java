package com.tumbleweed.io.bean;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.util.Collection;
import java.util.Map;

public class BootstrapContextHelper {
    private static ApplicationContext appContext;

    public static void setAppContext(ApplicationContext appContext) {
        BootstrapContextHelper.appContext = appContext;
    }

    public static ApplicationContext getAppContext() {
        return appContext;
    }

    public static <T> Map<String, T> getBeans(Class<T> beanClass) {
        Map<String, T> beans = appContext.getBeansOfType(beanClass);

        return beans;
    }

    public static <T> T getBean(Class<T> beanClass) {
        Collection<T> beans = appContext.getBeansOfType(beanClass).values();
        if (beans.isEmpty() && appContext.getParent() != null) {
            beans = appContext.getParent().getBeansOfType(beanClass).values();
        }
        T bean = beans.iterator().next();
        if (bean == null) {
            throw new IllegalArgumentException("Couldn't locate bean of " + beanClass + " type");
        }
        return bean;
    }

    public static Object getBean(String name) {
        Object bean = appContext.getBean(name);
        if (bean == null && appContext.getParent() != null) {
            bean = appContext.getParent().getBean(name);
        }

        return bean;
    }

    public static <T> T getBean(String name, Class<T> beanClass) {
        Map<String, T> beansOfType = appContext.getBeansOfType(beanClass);
        if (beansOfType.isEmpty() && appContext.getParent() != null) {
            beansOfType = appContext.getParent().getBeansOfType(beanClass);
        }

        T bean = null;
        for (Map.Entry<String, T> beanOfType : beansOfType.entrySet()) {
            if (name.equals(beanOfType.getKey())) {
                bean = beanOfType.getValue();
                break;
            }
        }

        return bean;
    }

    public static boolean hasAppContext() {
        return BootstrapContextHelper.appContext != null;
    }

    public static Resource getResource(String location) {
        return BootstrapContextHelper.appContext.getResource(location);
    }

}
