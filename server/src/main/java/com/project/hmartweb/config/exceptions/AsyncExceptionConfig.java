package com.project.hmartweb.config.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.reflect.Method;


@EnableAsync
@Configuration
public class AsyncExceptionConfig implements AsyncUncaughtExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncExceptionConfig.class);

    @Override
    public void handleUncaughtException(@NonNull Throwable throwable, @NonNull Method method, @NonNull Object... objects) {
        LOGGER.error("Exception in async method: {}", method.getName(), throwable);
    }
}
