package com.yangzhou.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldUtil {
  /**
   * 获取类clz的所有属性，包含自有属性和继承属性
   *
   * @param clz
   * @return
   */
  @SuppressWarnings("rawtypes")
  public static Field[] getAllFields(Class clz) {
    return getAllFields(clz, Object.class);
  }

  /**
   *
   * @param clz         待获取属性类
   * @param topClass    clz有多级继承时，topClass决定获取父类属性时，到哪一层父类为止。如：SearchParameter的子类，
   *                    不需要获取SearchParameter的属性，就需要将SearchParameter设为topClass，获取属性时，就到SearchParameter的子类为止。
   * @param skipClasses 类的继承链中，需要忽略的类
   * @return
   */
  @SuppressWarnings("rawtypes")
  public static Field[] getAllFields(Class clz, Class topClass, Class... skipClasses) {
    final List<Class> skipClassList = Arrays.asList(skipClasses);

    final List<Field> filList = new ArrayList<>();
    while (!clz.equals(topClass)) {
      if (!skipClassList.contains(clz)) {
        final Field[] fields = clz.getDeclaredFields();
        for (final Field field : fields) {
          filList.add(field);
        }
      }
      clz = clz.getSuperclass();
    }
    return filList.toArray(new Field[filList.size()]);
  }

  /**
   * 将对象转换为Map，map的key为对象的属性名，值为属性值。
   *
   * @param <T>
   * @param obj 待转换为Map的对象
   * @return
   */
  public static <T> Map<String, String> toMap(T obj) {
    return toMap(obj, Object.class);
  }

  /**
   * 将对象转换为Map，map的key为对象的属性名，值为属性值。
   *
   * @param <T>
   * @param obj         待转换为Map的对象
   * @param topClass    clz有多级继承时，topClass决定获取父类属性时，到哪一层父类为止。如：SearchParameter的子类，
   *                    不需要获取SearchParameter的属性，就需要将SearchParameter设为topClass，获取属性时，就到SearchParameter的子类为止。
   * @param skipClasses 类的继承链中，需要忽略的类
   * @return
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <T> Map<String, String> toMap(T obj, Class topClass, Class... skipClasses) {
    final Class<T> clz    = (Class<T>) obj.getClass();
    final Field[]             fields = getAllFields(clz, topClass, skipClasses);
    final Map<String, String> result = new HashMap<>();
    for (final Field field : fields) {
      field.setAccessible(true);
      try {
        final Object fieldValue = field.get(obj);
        if (fieldValue != null) {
          final String value = fieldValue.toString();
          if (!StringUtil.isEmpty(value)) {
            result.put(field.getName(), value);
          }
        }
      } catch (final Throwable e) {
        e.printStackTrace();
      }
    }
    return result;

  }
}
