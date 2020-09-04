package com.yangzhou.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableCaching
@Order(value = Integer.MIN_VALUE)
public class RedisConfigure extends CachingConfigurerSupport {
  private static final ObjectMapper objectMapper = JacksonConfiguration.createMapperObjectWithClassInfo();

  @Bean
  public CacheManager cacheManager(RedisConnectionFactory factory) {
    final RedisCacheConfiguration configuration = RedisCacheConfiguration
            .defaultCacheConfig()
            .disableCachingNullValues()
            .serializeKeysWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)))
            .entryTtl(Duration.ofHours(2));

    // 设置一个初始化的缓存空间set集合
    final Set<String> cacheNames = new HashSet<>();
    cacheNames.add("my-redis-cache1");
    cacheNames.add("user");

    // 对每个缓存空间应用不同的配置
    final Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
    configMap.put("my-redis-cache1", configuration);
    configMap.put("user", configuration.entryTtl(Duration.ofSeconds(120)));

    return RedisCacheManager
            .builder(RedisCacheWriter.nonLockingRedisCacheWriter(factory))
            .initialCacheNames(cacheNames)
            .withInitialCacheConfigurations(configMap)
            .cacheDefaults(configuration).build();
  }

  @Override
  @Bean
  public KeyGenerator keyGenerator() {
    return (target, method, params) -> {
      final StringBuilder sb = new StringBuilder();
      final Cacheable isCacheable = method.getAnnotation(Cacheable.class);
      if (isCacheable != null) {
        sb.append(isCacheable.value());
      }

      final CachePut isCachePut = method.getAnnotation(CachePut.class);
      if (isCachePut != null) {
        sb.append(isCachePut.value());
      }

      final CacheEvict isCacheEvict = method.getAnnotation(CacheEvict.class);
      if (isCacheEvict != null) {
        sb.append(isCacheEvict.value());
      }

      for (final Object obj : params) {
        sb.append(":").append(obj.toString());
      }
      return sb.toString();
    };
  }

  @Autowired
  RedisTemplate<String, String> redisTemplate;

  @Bean
  public ValueOperations<String, String> opsForValue() {
    return redisTemplate.opsForValue();
  }

  @Bean
  public ListOperations<String, String> opsForList() {
    return redisTemplate.opsForList();
  }

  @Bean
  public SetOperations<String, String> opsForSet() {
    return redisTemplate.opsForSet();
  }
}
