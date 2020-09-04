package com.yangzhou.frame.service;

public interface ExtensionTypeService {
  <T> T parse(String className, String name, String value);
}
