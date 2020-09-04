package com.yangzhou.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import io.github.jhipster.async.ExceptionHandlingAsyncTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfiguration implements AsyncConfigurer, SchedulingConfigurer {

	private final Logger log = LoggerFactory.getLogger(AsyncConfiguration.class);
	private final TaskExecutionProperties taskExecutionProperties;

	public AsyncConfiguration(TaskExecutionProperties taskExecutionProperties) {
		this.taskExecutionProperties = taskExecutionProperties;
	}

	@Override
	@Bean(name = "taskExecutor")
	public Executor getAsyncExecutor() {
		log.debug("Creating Async Task Executor");
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(taskExecutionProperties.getPool().getCoreSize());
		executor.setMaxPoolSize(taskExecutionProperties.getPool().getMaxSize());
		executor.setQueueCapacity(taskExecutionProperties.getPool().getQueueCapacity());
		executor.setThreadNamePrefix(taskExecutionProperties.getThreadNamePrefix());
		return new ExceptionHandlingAsyncTaskExecutor(executor);
	}

	@Bean(name = "taskExecutor1")
	public Executor getAsyncExecutor1() {
		log.debug("Creating Async Task Executor1");
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(5);
		executor.setQueueCapacity(10000);
		executor.setThreadNamePrefix("zzb-Executor1-");
		return new ExceptionHandlingAsyncTaskExecutor(executor);
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new SimpleAsyncUncaughtExceptionHandler();
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(scheduledTaskExecutor());
	}

	@Bean
	public Executor scheduledTaskExecutor() {
		return Executors.newScheduledThreadPool(taskExecutionProperties.getPool().getCoreSize());
	}
}
