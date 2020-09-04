package com.yangzhou.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.yangzhou.sqlInject.MySqlInjector;

@EnableTransactionManagement
@Configuration
@MapperScan("com.yangzhou.**.mapper")
public class MybatisPlusConfig {
  @Bean
  public PaginationInterceptor paginationInterceptor() {
    return new PaginationInterceptor();
  }

  @Bean
  @Profile({ "dev", "test" })
  public PerformanceInterceptor performanceInterceptor() {
    return new PerformanceInterceptor();
  }

  @Bean
  public GlobalConfig globalConfig() {
    final GlobalConfig config = new GlobalConfig();
    config.setSqlInjector(null);
    return config;
  }

  @Bean
  public ISqlInjector sqlInjector() {
    return new MySqlInjector();
  }

}
