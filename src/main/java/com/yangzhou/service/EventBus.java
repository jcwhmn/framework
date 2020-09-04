package com.yangzhou.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.CollectionUtils;
import com.yangzhou.annotation.Subscribe;
import com.yangzhou.domain.ProcessorProxy;
import com.yangzhou.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

public class EventBus {
  private static EventBus bus = new EventBus();
  private Map<TypeMethod, List<ProcessorProxy>> proxies = new ConcurrentHashMap<>(10);
  public static EventBus instance() {
    return bus;
  }

  public void registry(Subscribe subscribe, ProcessorProxy proxy) {
    TypeMethod typeMethod = new TypeMethod(subscribe.type(), subscribe.method().toString());
    List<ProcessorProxy> proxyList;
    if(proxies.containsKey(typeMethod)) {
      proxyList = proxies.get(typeMethod);
    }else {
      proxyList = new ArrayList<>();
      proxies.put(typeMethod, proxyList);
    }
    proxyList.add(proxy);
  }

  public void post(String type, EventType method, Object... args) {
    TypeMethod typeMethod = new TypeMethod(type, method.toString());
    List<ProcessorProxy> proxyList = proxies.get(typeMethod);
    if(!CollectionUtils.isEmpty(proxyList)) {
      proxyList.forEach(proxy -> proxy.execute(args));
    }
  }
  
  @EqualsAndHashCode
  @Getter
  @Setter
  @AllArgsConstructor
  class TypeMethod{
    String type;
    String eventType;
  }
}
