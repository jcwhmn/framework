package com.yangzhou.domain;

import com.yangzhou.service.EntityExtensionManager;
import com.yangzhou.service.ExtensionManager;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ProcessorProxy {
  final static ExtensionManager extensionManager = EntityExtensionManager.instance();
  private final String serviceName;
  private final String methodName;

  public ProcessorProxy(String serviceName, String methodName) {
    this.serviceName = serviceName;
    this.methodName = methodName;
  }

  public void execute(Object... args) {
    extensionManager.execute(serviceName, methodName, args);
  }
}
