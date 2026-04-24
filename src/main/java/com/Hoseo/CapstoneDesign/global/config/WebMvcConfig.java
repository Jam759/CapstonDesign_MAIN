package com.Hoseo.CapstoneDesign.global.config;

import com.Hoseo.CapstoneDesign.global.logging.HandlerMetadataInterceptor;
import com.Hoseo.CapstoneDesign.global.logging.MdcTaskDecorator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//Spring MVC 비동기 요청/SSE 응답 설정
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final HandlerMetadataInterceptor handlerMetadataInterceptor;

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("mvc-vt-");
        executor.setVirtualThreads(true);
        executor.setTaskDecorator(new MdcTaskDecorator());
        configurer.setDefaultTimeout(60_000L);
        configurer.setTaskExecutor(executor);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(handlerMetadataInterceptor).addPathPatterns("/**");
    }
}
