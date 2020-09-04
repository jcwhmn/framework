package com.yangzhou.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Json服务
 *
 * @author Jiang Chuanwei
 *
 */
public interface JsonService {
  /**
   *
   * 生成给定对象的Json字符串。Json中包含T类型信息。如：<br/>
   * {@code {\"@class\":\"com.zzb.service.TestUser\",\"id\":1000,\"login\":\"test\",\"firstName\":\"wang\",\"lastName\":\"anshi\",\"activated\":false,\"authorities\":[\"java.util.HashSet\",[]]}}<br/>
   *
   * 无须给定类型信息，即可反序列化该方法生成的Json字符串，如：<br/>
   * {@code TestUser user = jsonService.toBean(json);}
   *
   * @param <T> 任意对象类型
   * @param obj
   * @return Json字符串
   */
  <T> String toJson(T obj);

  /**
   * 生成给定对象的Json字符串。Json中不包含类型信息。如：
   * {@code {\"id\":1000,\"login\":\"test\",\"firstName\":\"wang\",\"lastName\":\"anshi\",\"activated\":false,\"authorities\":[]}}
   * <br/>
   * 反序列该Json时，需要指定类型信息：<br/>
   * {@code TestUser user = jsonService.toBean(json, TestUser.class);}
   *
   * @param <T> 任意对象类型
   * @param obj
   * @return Json字符串
   */
  <T> String toJsonWithoutClassInfo(T obj);

  /**
   * 从Json串生成对象。Json串中需包含类型信息。如：<br/>
   * {@code {\"@class\":\"com.zzb.service.TestUser\",\"id\":1000,\"login\":\"test\",\"firstName\":\"wang\",\"lastName\":\"anshi\",\"activated\":false,\"authorities\":[\"java.util.HashSet\",[]]}}<br/>
   *
   * @param <T>  任意对象类型
   * @param json 有效json字符串
   * @return 从json串解析出的对象
   */
  <T> T toBean(String json);

  /**
   *
   * @param <T>
   * @param json
   * @param clz
   * @return
   */
  <T> T toBean(String json, Class<T> clz);

  /**
   *
   * @param <T>
   * @param map
   * @param clz
   * @return
   */
  <T> T toBean(Object map, Class<T> clz);

  /**
   *
   * @param <U>
   * @param <V>
   * @param json
   * @param clazzU
   * @param clazzV
   * @return
   */
  <U, V> Map<U, V> toMap(String json, Class<U> clazzU, Class<V> clazzV);

  /**
   *
   * @param <T>
   * @param json
   * @param clazz
   * @return
   */
  <T> Map<String, T> toMap(String json, Class<T> clazz);

  /**
   *
   * @param <T>
   * @param json
   * @param clazz
   * @return
   */
  <T> List<T> toList(String json, Class<T> clazz);

  /**
   *
   * @param <T>
   * @param json
   * @param clazz
   * @return
   */
  <T> Set<T> toSet(String json, Class<T> clazz);

  /**
   *
   * @param <T>
   * @param json
   * @return
   */
  <T> T toBean(byte[] json);

  /**
   *
   * @param <U>
   * @param <V>
   * @param json
   * @param clazzU
   * @param clazzV
   * @return
   */
  <U, V> U to2ClassObject(String json, Class<U> clazzU, Class<V> clazzV);

  /**
   *
   * @param <U>
   * @param <V>
   * @param <W>
   * @param json
   * @param clazzU
   * @param clazzV
   * @param clazzW
   * @return
   */
  <U, V, W> U to3ClassObject(String json, Class<U> clazzU, Class<V> clazzV, Class<W> clazzW);

  /**
   * 输入未格式化Json字符串，返回经过格式化的Json字符串
   *
   * @param json
   * @return
   */
  String formatJsonString(String json);

  <U, V, W, X> U to4ClassObject(String json, Class<U> clazzU, Class<V> clazzV, Class<W> clazzW, Class<X> clazzX);

  <T> Map<String, List<T>> toListMap(String json, Class<T> clazz);
}