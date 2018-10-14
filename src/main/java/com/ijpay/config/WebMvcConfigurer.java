package com.ijpay.config;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.ijpay.interceptor.AliPayInterceptor;
import com.ijpay.interceptor.CharacterEncodInterceptor;
import com.ijpay.interceptor.WxPayInterceptor;

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurationSupport {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AliPayInterceptor()).addPathPatterns("/alipay/**");
        registry.addInterceptor(new WxPayInterceptor()).addPathPatterns("/wxpay/**","/wxsubpay/**");
        registry.addInterceptor(new CharacterEncodInterceptor()).addPathPatterns("/unionpay/**");
        super.addInterceptors(registry);
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        converters.add(responseBodyConverter());
    }
    
    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        return converter;
    }
}

