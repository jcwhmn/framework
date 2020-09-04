package com.yangzhou.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Cache管理服务
 * 
 * @author Jiang Chuanwei
 *
 */
public interface CacheService {
  /**
   * 增加或修改Cache记录
   * 
   * @param <T>   任务对象类型
   * @param key
   * @param value
   */
	<T> void set(String key, T value);

  /**
   * 获取指定key的Cache值
   * 
   * @param <T>   任务对象类型
   * @param key
   * @return
   */
	<T> T get(String key);

  /**
   * 增加或修改指定name:key的Cache值
   * 
   * @param <T>   任务对象类型
   * @param name
   * @param key
   * @param obj
   */
	<T> void set(String name, String key, T obj);

  /**
   * 获取指定name:key的Cache值
   * 
   * @param <T>   任务对象类型
   * @param name
   * @param key
   * @return
   */
	<T> T get(String name, String key);

  /**
   * 设置指定name的cache的过期时间
   * 
   * @param name
   * @param expire 单位毫秒
   * @return
   */
	boolean expire(String name, long expire);

  /**
   * 向指定key的list的左端推入值
   * 
   * @param <T>   任务对象类型
   * @param key
   * @param obj
   * @return
   */
	<T> long lpushList(String key, T obj);

  /**
   * 向指定key的list的右端推入值
   * 
   * @param <T>   任务对象类型
   * @param key
   * @param obj
   * @return
   */
	<T> long rpushList(String key, T obj);

  /**
   * 从指定key的list的左端获取值
   * 
   * @param <T>   任务对象类型
   * @param key
   * @return
   */
	<T> T lpopList(String key);

  /**
   * 从指定key的list的右端获取值
   * 
   * @param <T>   任务对象类型
   * @param key
   * @return
   */
	<T> T rpopList(String key);

  /**
   * 向指定name:key的list的左端推入值
   * 
   * @param <T>   任务对象类型
   * @param name
   * @param key
   * @param obj
   * @return
   */
	<T> long lpushList(String name, String key, T obj);

  /**
   * 向指定name:key的list的右端推入值
   * 
   * @param <T>   任务对象类型
   * @param name
   * @param key
   * @param obj
   * @return
   */
	<T> long rpushList(String name, String key, T obj);

  /**
   * 从指定name:key的list的左端获取值
   * 
   * @param <T>   任务对象类型
   * @param name
   * @param key
   * @return
   */
	<T> T lpopList(String name, String key);

  /**
   * 从指定name:key的list的右端获取值
   * 
   * @param <T>   任务对象类型
   * @param name
   * @param key
   * @return
   */
	<T> T rpopList(String name, String key);

  /**
   * 从指定key的list的指定index获取值
   * 
   * @param <T>   任务对象类型
   * @param key
   * @param index
   * @return
   */
	<T> T getAtList(String key, long index);

  /**
   * 从指定name:key的list的指定index获取值
   * 
   * @param <T>   任务对象类型
   * @param name
   * @param key
   * @param index
   * @return
   */
	<T> T getAtList(String name, String key, long index);

  /**
   * 向指定key的list的指定index设置值
   * 
   * @param <T>   任务对象类型
   * @param key
   * @param index
   * @param obj
   */
	<T> void setAtList(String key, long index, T obj);

  /**
   * 向指定name:key的list的指定index设置值
   * 
   * @param <T>   任务对象类型
   * @param name
   * @param key
   * @param index
   * @param obj
   */
	<T> void setAtList(String name, String key, long index, T obj);

  /**
   * 删除指定name的cache
   * 
   * @param cacheName
   */
	void delete(String cacheName);

  /**
   * 删除指定name:key的cache
   * 
   * @param cacheName
   * @param key
   */
	void delete(String cacheName, String key);

  /**
   * 删除指定name下，一系列key的cache
   * 
   * @param cacheName
   * @param keys
   */
	void delete(String cacheName, Collection<? extends Serializable> keys);

  /**
   * 当指定key的cache存在时，获取该值。当指定key的cache不存在时，执行supplier方法，并将该方法的返回值 设置为key的cache
   * 
   * @param <T>   任务对象类型
   * @param key
   * @param supplier
   * @return
   */
	<T> T getOrSet(String key, Supplier<T> supplier);

  /**
   * 当指定name:key的cache存在时，获取该值。当指定name:key的cache不存在时，执行supplier方法，并将该方法的返回值
   * 设置为name:key的cache
   * 
   * @param <T>   任务对象类型
   * @param name
   * @param key
   * @param supplier
   * @return
   */
	<T> T getOrSet(String name, String key, Supplier<T> supplier);

  /**
   * 将一系列values插入指定key的集合
   * 
   * @param <T>   任务对象类型
   * @param key
   * @param values
   */
	<T> void addToSet(String key, T... values);

  /**
   * 将一系列values插入指定name:key的集合
   * @param <T>   任务对象类型
   * @param name
   * @param key
   * @param values
   */
	<T> void addToSet(String name, String key, T... values);

  /**
   * 获取cache中指定key的集合的所有记录
   * 
   * @param <T>   任务对象类型
   * @param key
   * @return
   */
	<T> Set<T> getSetMembers(String key);

  /**
   * 获取cache中指定name:key的集合的所有记录
   * 
   * @param <T>   任务对象类型
   * @param name
   * @param key
   * @return
   */
	<T> Set<T> getSetMembers(String name, String key);

	/**
   * 计数器减少1.当当前name:key不存在时，返回-1
	 * 
	 * @param name
	 * @param key
	 * @return
	 */
  long derement(String name, String key);

  /**
   * 计数器减少1.当当前key不存在时，返回-1
   * 
   * @param key
   * @return
   */
  long decrement(String key);

  /**
   * 计数器增加1.当当前name:key不存在时，返回1
   * 
   * @param name
   * @param key
   * @return
   */
  long increment(String name, String key);

  /**
   * 计数器增加1.当当前key不存在时，返回1
   * @param key
   * @return
   */
  long increment(String key);

  /**
   * 获取两个Set的交集
   * @param <T>   任务对象类型
   * @param source
   * @param target
   * @return
   */
  <T> Set<T> intersect(Set<T> source, Set<T> target);

  /**
   * 增加一条记录到set中
   * @param <T>   任务对象类型
   * @param key
   * @param obj
   */
  <T> void addToSet(String key, T obj);

  /**
   * 删除命名空间为name的所有key
   * @param name
   */
  void deleteName(final String name);

  /**
   * 获取命名空间为name的所有对象
   * @param <T>
   * @param name
   * @return
   */
  <T> Set<T> getName(String name);
}