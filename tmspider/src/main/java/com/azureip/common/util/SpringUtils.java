package com.azureip.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
public class SpringUtils implements ApplicationContextAware {

    // Spring应用上下文环境
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    // 获取应用上下文环境
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // 通过name获取Bean
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    // 通过class获取Bean
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    // 通过name, class返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }
}
