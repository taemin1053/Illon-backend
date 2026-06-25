package com.career.analytics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AppConfig {

    @Bean(name = "aiAnalysisExecutor")
    public Executor aiAnalysisExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 기본 대기 쓰레드 수
        executor.setMaxPoolSize(30);  // 최대 확장 쓰레드 수
        executor.setQueueCapacity(100); // 큐 용량
        executor.setThreadNamePrefix("ai-async-");
        executor.initialize();
        return executor;
    }
}
