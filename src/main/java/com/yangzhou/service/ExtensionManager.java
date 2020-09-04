package com.yangzhou.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yangzhou.domain.StringPair;

public interface ExtensionManager {
  <T> T getById(long id, Class<T> clz);
  <T> List<T> list(Class<T> clz);
  <T> List<T> list(Class<T> clz, QueryWrapper<T> queryWrapper);
  <T> T getById(long id, String serviceName);
  <T> Collection<T> list(Map<String, Object> condition, Class<T> clz);
  <T> T getOne(Map<String, Object> condition, Class<T> clz);
  <T> Collection<T> list(Class<T> clz, StringPair... pairs);
  <T> T getOne(Class<T> clz, StringPair... pairs);
  <T> boolean delete(Map<String, Object> conditions, Class<T> clz);
  <T> boolean delete(Class<T> clz, StringPair[] pairs);

  <T> List<T> list(Class clz, String method, Object... args);
  <T> List<T> list(String serviceName, String method, Object... args);
  <T> T value(String serviceName, String methodName, Object... arg);
  <T> T value(Class clz, String methodName, Object... args);

  void execute(String serviceName, String methodName, Object... entities);
}
