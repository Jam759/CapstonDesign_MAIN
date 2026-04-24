package com.Hoseo.CapstoneDesign.global.config;

import com.Hoseo.CapstoneDesign.global.logging.MdcTaskDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//@Async 비동기 작업 실행 설정
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    @Bean(name = "notificationExecutor")
    public AsyncTaskExecutor notificationExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("notification-vt-");
        executor.setVirtualThreads(true);
        executor.setTaskDecorator(new MdcTaskDecorator());
        return executor;
    }
}
