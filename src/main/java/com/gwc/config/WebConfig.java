package com.gwc.config;

import com.gwc.utils.KamiLoginInterceptor;
import com.gwc.utils.UserLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new KamiLoginInterceptor())
                .addPathPatterns("/kami/**")//拦截所有请求
                .excludePathPatterns(
                        "/kami/login"
                );//放行登录

        registry.addInterceptor(new UserLoginInterceptor())
                .addPathPatterns("/user/**")//拦截所有请求
                .excludePathPatterns("/user/login/**")
                .excludePathPatterns("/user/forgot-password/**")
                .excludePathPatterns("/user/register/**");
    }//登录放行
}