package com.yangzhou.rabbitmq;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import javax.annotation.PostConstruct;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.yangzhou.service.CacheService;
import com.yangzhou.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageSender implements RabbitTemplate.ConfirmCallback, ReturnCallback {
  @Autowired private RabbitTemplate template;
  @Autowired CacheService           cacheService;
  protected final static String     QUEUE_CACHE_NAME = "RabbitMQ";

  @PostConstruct public void init() {
    template.setConfirmCallback(this);
    template.setReturnCallback(this);
    template.setReplyTimeout(20000);
  }

  @Override public void confirm(CorrelationData correlationData, boolean ack, String cause) {
    if (ack) {
      log.info("消息发送成功:{}", correlationData);
    } else {
      log.info("消息发送失败:{}", cause);
    }
  }

  @Override public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
    log.info("{} 发送失败", message.getMessageProperties().getCorrelationId());
  }

  /**
   * 使用Direct方式发送一条字符串信息
   *
   * @param queue      直接发送到Queue。
   * @param str 发送字符串信息
   */
  public void sendDirect(String queue, Object obj, CountDownLatch latch) {
    sendDirect(queue, obj);
    latch.countDown();
  }

  public void sendDirect(String queue, Object obj) {
    final String uuid = UUID.randomUUID().toString();
    final CorrelationData correlationData = new CorrelationData(uuid);

    template.convertAndSend(queue, obj, correlationData);
    cacheService.addToSet(QUEUE_CACHE_NAME, uuid);
  }

  public void send(String exchange, String bindingKeyString, Object obj) {
    final String          uuid            = UUID.randomUUID().toString();
    final CorrelationData correlationData = new CorrelationData(uuid);
    template.convertAndSend(exchange, bindingKeyString, obj, correlationData);
    cacheService.addToSet(QUEUE_CACHE_NAME, uuid);
  }

  public <U, V> U sendAndReceive(String exchange, String bindingKeyString, V message) {
    log.info("{} Start send and  Receive. message ={}", LocalDateTime.now(), message);
    final String          uuid            = UUID.randomUUID().toString();
    final CorrelationData correlationData = new CorrelationData(uuid);
    return (U) template.convertSendAndReceive(exchange, bindingKeyString, message, correlationData);
  }

  public <U, V> U sendAndReceive(String exchange, String bindingKeyString, V message, long waitTimeOut) {
    log.info("{} Start send and  Receive. message ={}. timeout = {}", LocalDateTime.now(), message, waitTimeOut);
    final RabbitTemplate prototypeTemplate = (RabbitTemplate) SpringUtil.getBean("prototypeRabbitTemplate");
    prototypeTemplate.setReplyTimeout(waitTimeOut * 1000);
    return (U) prototypeTemplate.convertSendAndReceive(exchange, bindingKeyString, message, new CorrelationData(UUID.randomUUID().toString()));
  }

  public <U, V> U sendAndReceive(String exchange, String bindingKeyString, V message, long waitTimeOut, CountDownLatch latch) {
    try {
      return sendAndReceive(exchange, bindingKeyString, message, waitTimeOut);
    } finally {
      latch.countDown();
    }
  }

  public String sendAndReceive(String topic, String message) {
    return (String) template.convertSendAndReceive(topic, message);
  }
}
