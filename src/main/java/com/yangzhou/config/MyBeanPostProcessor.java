package com.yangzhou.config;

import java.lang.reflect.Method;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import com.yangzhou.annotation.Subscribe;
import com.yangzhou.domain.ProcessorProxy;
import com.yangzhou.service.EventBus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

  @Nullable
  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    final Class cla = bean.getClass();
    final Method[] methods = cla.getDeclaredMethods();
    for (final Method method : methods) {
      method.setAccessible(true);
      final Subscribe subscriber = AnnotationUtils.findAnnotation(method, Subscribe.class);
      if (subscriber != null) {
        final ProcessorProxy processorProxy = new ProcessorProxy(cla.getSimpleName(), method.getName());
        EventBus.instance().registry(subscriber, processorProxy);
      }
    }
    return bean;
  }


}
