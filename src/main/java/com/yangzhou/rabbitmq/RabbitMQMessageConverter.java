package com.yangzhou.rabbitmq;

import java.lang.reflect.Type;
import java.util.UUID;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import com.yangzhou.service.JacksonJsonService;
import com.yangzhou.service.JsonService;

public class RabbitMQMessageConverter extends Jackson2JsonMessageConverter {
  JsonService jsonService = new JacksonJsonService();

  @Override public Object fromMessage(Message message) throws MessageConversionException {
    return jsonService.toBean(message.getBody());
  }

  @Override protected Message createMessage(Object object, MessageProperties messageProperties, Type genericType)
        throws MessageConversionException {
    return MessageBuilder.withBody(jsonService.toJson(object).getBytes())
          .setContentType(MessageProperties.CONTENT_TYPE_JSON)
          .setMessageId(UUID.randomUUID().toString())
          .build();
  }
}
