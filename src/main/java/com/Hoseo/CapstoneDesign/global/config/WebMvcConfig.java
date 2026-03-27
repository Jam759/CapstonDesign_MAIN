package com.Hoseo.CapstoneDesign.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//Spring MVC 비동기 요청/SSE 응답 설정
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(60_000L);
        configurer.setTaskExecutor(new VirtualThreadTaskExecutor("mvc-vt-"));
    }

}
