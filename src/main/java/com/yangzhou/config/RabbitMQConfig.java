package com.yangzhou.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import com.yangzhou.rabbitmq.RabbitMQMessageConverter;

@Configuration
public class RabbitMQConfig {
  private final static long TIMEOUT = 1000000L; // timeout时间1000秒

  @Bean @Primary public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(new RabbitMQMessageConverter());
    rabbitTemplate.setReceiveTimeout(TIMEOUT);
    rabbitTemplate.setReplyTimeout(TIMEOUT);

    return rabbitTemplate;
  }

  @Bean("prototypeRabbitTemplate") @Scope("prototype") public RabbitTemplate prototypeRabbitTemplate(ConnectionFactory connectionFactory) {
    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(new RabbitMQMessageConverter());

    return rabbitTemplate;
  }

  @Bean public Jackson2JsonMessageConverter customConverter() {
    return new RabbitMQMessageConverter();
  }
}
