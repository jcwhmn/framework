package com.yangzhou.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service("cacheService")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RedisService implements CacheService {
	private static final String WildKey = ":*";
  @Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private ValueOperations<String, String> opsForValue;
	@Autowired
	private ListOperations<String, String> opsForList;
	@Autowired
	private SetOperations<String, String> opsForSet;
  private static JsonService                         jsonService = new JacksonJsonService();

  @Override
	public <T> void set(final String key, final T value) {
		opsForValue.set(key, jsonService.toJson(value));
	}

	@Override
	public <T> T get(final String key) {
	  final String str = opsForValue.get(key);
		return (T) jsonService.toBean(opsForValue.get(key));
  }

	@Override
	public <T> void set(String name, String key, T obj) {
		set(combineKey(name, key), obj);
  }

	@Override
	public <T> T get(String name, String key) {
    return get(combineKey(name, key));
  }

	@Override
  public <T> Set<T> getName(String name){
	  final Set<String> keys = redisTemplate.keys(name + WildKey);
	  return (Set<T>) opsForValue.multiGet(keys).stream().map(jsonService::toBean).collect(Collectors.toSet());
	}
	@Override
	public <T> T getOrSet(String key, Supplier<T> supplier) {
/*		T obj = get(key);
		if (obj == null) {
			obj = supplier.get();
			if (obj != null) {
        set(key, obj);
      }
		}*/
        T obj = supplier.get();
		return obj;
	}

	@Override
	public <T> T getOrSet(String name, String key, Supplier<T> supplier) {
		return getOrSet(combineKey(name, key), supplier);
	}

  @Override
	public boolean expire(final String name, long expire) {
    return redisTemplate.expire(name, expire, TimeUnit.SECONDS);
	}

	@Override
	public <T> long lpushList(String key, T obj) {
		return opsForList.leftPush(key, jsonService.toJson(obj));
	}

	@Override
	public <T> long rpushList(String key, T obj) {
		return opsForList.rightPush(key, jsonService.toJson(obj));
	}

	@Override
	public <T> T lpopList(final String key) {
		return jsonService.toBean(opsForList.leftPop(key));
	}

	@Override
	public <T> T rpopList(final String key) {
		return jsonService.toBean(opsForList.rightPop(key));
	}

	@Override
	public <T> long lpushList(String name, String key, T obj) {
		return opsForList.leftPush(combineKey(name, key), jsonService.toJson(obj));
	}

	@Override
	public <T> long rpushList(String name, String key, T obj) {
		return opsForList.rightPush(combineKey(name, key), jsonService.toJson(obj));
	}

	@Override
	public <T> T lpopList(String name, final String key) {
		return jsonService.toBean(opsForList.leftPop(combineKey(name, key)));
	}

	@Override
	public <T> T rpopList(String name, final String key) {
		return jsonService.toBean(opsForList.rightPop(combineKey(name, key)));
	}

	@Override
	public <T> T getAtList(String key, long index) {
		return jsonService.toBean(opsForList.index(key, index));
	}

	@Override
	public <T> T getAtList(String name, String key, long index) {
		return jsonService.toBean(opsForList.index(combineKey(name, key), index));
	}

	@Override
	public <T> void setAtList(String key, long index, T obj) {
		opsForList.set(key, 0, jsonService.toJson(obj));
	}

	@Override
	public <T> void setAtList(String name, String key, long index, T obj) {
		opsForList.set(combineKey(name, key), 0, jsonService.toJson(obj));
	}

	@Override
	public <T> void addToSet(String key, T... values) {
	  final String[] strValues = new String[values.length];
	  for(int i = 0 ; i < values.length ; i++) {
	    strValues[i] = jsonService.toJson(values[i]);
	  }
		opsForSet.add(key, strValues);
	}

	 @Override
  public <T> void addToSet(String key, T obj) {
	   opsForSet.add(key, jsonService.toJson(obj));
	 }

	private void addToSet(String key, String... values) {
	  opsForSet.add(key, values);
	}

	public <T> void addToSet(String name, String key, T obj) {
	  addToSet(combineKey(name, key), obj);
	}

	@Override
	public <T> void addToSet(String name, String key, T... values) {
		addToSet(combineKey(name, key), values);
	}

	@Override
	public <T> Set<T> getSetMembers(String key) {
		return stringSetToTSet(opsForSet.members(key));
	}

  private <T> Set<T> stringSetToTSet(Set<String> members) {
    final Set<T> memberTs = new HashSet<>();
		members.forEach(member -> memberTs.add(jsonService.toBean(member)));
    return memberTs;
  }

	@Override
	public <T> Set<T> getSetMembers(String name, String key) {
		return getSetMembers(combineKey(name, key));
	}

	@Override
	public void delete(String key) {
    redisTemplate.delete(key);
	}

	@Override
  public void deleteName(final String name) {
	  redisTemplate.delete(redisTemplate.keys(name + WildKey));
	}
	@Override
	public void delete(String name, String key) {
	  redisTemplate.delete(combineKey(name, key));
	}

	public static final String combineKey(String name, String key) {
		return name + "::" + key;
	}

	public static final Collection<String> combineKey(final String name, Collection<? extends Serializable> keys) {
		final Collection<String> result = new HashSet<>();
		keys.forEach(key -> result.add(combineKey(name, key.toString())));
		return result;
	}

	@Override
	public void delete(String cacheName, Collection<? extends Serializable> keys) {
		redisTemplate.delete(combineKey(cacheName, keys));
	}

  final private Long INITCOUNT = 0l;

  @Override
  public long increment(String key) {
    return opsForValue.increment(key);
  }

  @Override
  public long increment(String name, String key) {
    return increment(combineKey(name, key));
  }
  @Override
  public long decrement(String key) {
    return opsForValue.decrement(key);
  }

  @Override
  public long derement(String name, String key) {
    return decrement(combineKey(name, key));
  }

  @Override
  public <T> Set<T> intersect(Set<T> source, Set<T> target){
    final String key = UUID.randomUUID().toString().replaceAll("-", "");
    final String key1 = UUID.randomUUID().toString().replaceAll("-", "");
    addToSet(key, tcollectionToStringArray(source));
    addToSet(key1, tcollectionToStringArray(target));
    final Set<String> resultSet = opsForSet.intersect(key, key1);
    delete(key);
    delete(key1);
    return stringSetToTSet(resultSet);
  }

  private <T> String[] tcollectionToStringArray(Collection<T> source) {
    final String[] result = new String[source.size()];
    int index = 0;
    for(final T obj: source) {
      result[index++] = jsonService.toJson(obj);
    }
    return result;
  }
  public <T> Set<T> intersect(String sourceKey, String targetKey){
    return (Set<T>) opsForSet.intersect(sourceKey, targetKey);
  }
}
