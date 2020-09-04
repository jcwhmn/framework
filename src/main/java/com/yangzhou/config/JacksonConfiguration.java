package com.yangzhou.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

@Configuration
@Order(value = Integer.MIN_VALUE)
public class JacksonConfiguration {

  /**
   * Support for Java date and time API.
   *
   * @return the corresponding Jackson module.
   */
  @Bean public JavaTimeModule javaTimeModule() {
    return createJavaTimeModule();
  }

  @Bean public Jdk8Module jdk8TimeModule() {
    return new Jdk8Module();
  }

  /*
   * Jackson Afterburner module to speed up serialization/deserialization.
   */
  @Bean public AfterburnerModule afterburnerModule() {
    return new AfterburnerModule();
  }

  /*
   * Module for serialization/deserialization of RFC7807 Problem.
   */
  @Bean
  ProblemModule problemModule() {
    return new ProblemModule();
  }

  /*
   * Module for serialization/deserialization of ConstraintViolationProblem.
   */
  @Bean
  ConstraintViolationProblemModule constraintViolationProblemModule() {
    return new ConstraintViolationProblemModule();
  }

  @Bean(name = "mapperObject") public ObjectMapper getObjectMapper() {
    return createMapperObject();
  }

  public static ObjectMapper createMapperObject() {
    final ObjectMapper om = new ObjectMapper();
    om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    // om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    // om.enableDefaultTyping(DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    om.registerModule(createJavaTimeModule());
    return om;
  }

  public static JavaTimeModule createJavaTimeModule() {
    final JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addSerializer(LocalDateTime.class,
            new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

    javaTimeModule.addDeserializer(LocalDateTime.class,
            new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

    return javaTimeModule;
  }

  public static ObjectMapper createMapperObjectWithClassInfo() {
    final ObjectMapper om = createMapperObject();
    om.enableDefaultTyping(DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    return om;
  }
}
