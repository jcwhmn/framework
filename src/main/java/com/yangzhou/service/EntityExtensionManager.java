package com.yangzhou.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yangzhou.domain.StringPair;
import com.yangzhou.errors.BadRequestException;
import com.yangzhou.util.SpringUtil;

/**
 *
 * @author Jiang Chuanwei
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class EntityExtensionManager implements ExtensionManager {
  private static final String SERVICE = "Service";
  private final static ExtensionManager instance = new EntityExtensionManager();

  public static ExtensionManager instance() {
    return instance;
  }

  @SuppressWarnings("rawtypes")
  public BaseServiceImpl getService(Class clz) {
    return getService(clz.getSimpleName());
  }

  @SuppressWarnings("rawtypes")
  public BaseServiceImpl getService(String serviceName) {
    serviceName = serviceName.substring(0, 1).toLowerCase() + serviceName.substring(1) + SERVICE;
    BaseServiceImpl service     = (BaseServiceImpl) SpringUtil.getBean(serviceName);
    if(service == null) {
      serviceName = serviceName + "Impl";
      service = (BaseServiceImpl) SpringUtil.getBean(serviceName);
    }
    return service;
  }

  @Override
  public <T> T getById(long id, Class<T> clz) {
    return  getById(id, clz.getSimpleName());
  }

  @Override
  public <T> T getById(long id, String serviceName) {
    final Object result = getService(serviceName).getById(id);
    if (result == null)
      return null;
    else return (T)result;
  }

  @Override
  public <T> T getOne(final Map<String, Object> condition, Class<T> clz) {
    final Collection<T> results = getService(clz).listByMap(condition);
    if (results.size() > 1)
      throw new BadRequestException(0, "请求1个对象，但返回了多个");
    else if (results.size() == 0) return null;
    else
      return results.iterator().next();
  }

  @Override
  public <T> T getOne(Class<T> clz, StringPair... pairs) {
    return getOne(StringPair.toMap(pairs), clz);
  }

  @Override
  public <T> boolean delete(Map<String, Object> conditions, Class<T> clz) {
    return getService(clz).removeByMap(conditions);
  }

  @Override
  public <T> boolean delete(Class<T> clz, StringPair... pairs) {
    return delete(StringPair.toMap(pairs), clz);
  }

  @Override
  public <T> List<T> list(Class<T> clz) {
    return getService(clz).list();
  }

  @Override
  public <T> List<T> list(Class<T> clz, QueryWrapper<T> queryWrapper) {
    return getService(clz).list(queryWrapper);
  }

  @Override public <T> Collection<T> list(final Map<String, Object> condition, Class<T> clz) {
    return getService(clz).listByMap(condition);
  }

  @Override public <T> Collection<T> list(Class<T> clz, StringPair... pairs) {
    return list(StringPair.toMap(pairs), clz);
  }

  @SuppressWarnings("rawtypes")
  @Override public <T> List<T> list(Class clz, String methodName, Object... args) {
    try {
      final BaseEntityService service = getService(clz);
      final Method            method  = getMethod(service, methodName, args);
      return (List<T>) method.invoke(service, args);
    } catch (final Exception e) {
      // todo 处理异常
      throw new BadRequestException(0, "");
    }
  }

  @SuppressWarnings("rawtypes")
  @Override public <T> List<T> list(String serviceName, String methodName, Object... args) {
    try {
      final BaseEntityService service = getService(serviceName);
      final Method            method  = getMethod(service, methodName, args);
      return (List<T>) method.invoke(service, args);
    } catch (final Exception e) {
      // todo 处理异常
      throw new BadRequestException(0, "");
    }
  }

  @SuppressWarnings("rawtypes")
  @Override public <T> T value(Class clz, String methodName, Object... args) {
    final BaseEntityService service = getService(clz);
    final Method            method  = getMethod(service, methodName, args);
    try {
      return (T) method.invoke(service, args);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      // todo 处理异常
      throw new BadRequestException(0, "");
    }
  }

  @SuppressWarnings("rawtypes")
  @Override
  public <T> T value(String serviceName, String methodName, Object... args) {
    try {
      final BaseEntityService service = getService(serviceName);
      final Method            method  = getMethod(service, methodName, args);
      return (T) method.invoke(service, args);
    } catch (final Exception e) {
      // todo 处理异常
      throw new BadRequestException(0, "");
    }
  }

  @SuppressWarnings("rawtypes")
  private Method getMethod(BaseEntityService service, String methodName, Object... args) {
    final Class clz    = service.getClass();
    Method method = null;
    try {
      if (args.length == 0) {
        method = clz.getMethod(methodName);
      } else {
        final Class[] clzs = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
          clzs[i] = args[i].getClass();
        }
        method = clz.getMethod(methodName, clzs);
      }
    } catch (final NoSuchMethodException e) {} catch (final Exception e) {
      // todo 处理异常
      throw new BadRequestException(0, "");
    }
    if (method == null) throw new BadRequestException(0, String.format("%s服务没有和参数列表匹配的方法%s", clz.getSimpleName(), methodName));
    return method;
  }

  @SuppressWarnings("rawtypes")
  public int count(String serviceName, StringPair... pairs) {
    final BaseServiceImpl service = getService(serviceName);
    return service.count(StringPair.toQueryWrapper(pairs));
  }

  public boolean exist(String serviceName, String field, String value) {
    return getService(serviceName).exist(field, value);
  }

  public boolean exist(String serviceName, long id) {
    return getService(serviceName).existById(id);
  }

  @Override public void execute(String serviceName, String methodName, Object... entities) {
    final BaseServiceImpl service = getService(serviceName);
    final Method method = getMethod(service, methodName, entities);
    try {
      method.invoke(service, entities);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new BadRequestException(0, "");
    }
  }
}
