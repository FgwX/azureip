package com.azureip.tmspider.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableWebMvc
@EnableScheduling
@ComponentScan({"com.azureip.tmspider"})
@MapperScan({"com.azureip.tmspider.mapper"})
public class TMConfig implements WebMvcConfigurer,AsyncConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/*").addResourceLocations("classpath:/static/");
    }

    @Override
    @Bean
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(8);
        executor.setKeepAliveSeconds(30);
        executor.initialize();
        return executor;
    }

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/static/");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/i").setViewName("/index.html");
//        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
//    }
}
