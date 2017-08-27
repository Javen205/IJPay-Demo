package com.ijpay.adapter;

import com.ijpay.interceptor.MyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MyWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {
    /**
     * 配置静态访问资源

     * @param registry

     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/my/**").addResourceLocations("classpath:/my/");
        super.addResourceHandlers(registry);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        registry.addInterceptor(new MyInterceptor()).addPathPatterns("/**").excludePathPatterns("/","/hi","/say", "/static/my/**");
        super.addInterceptors(registry);
    }
}

