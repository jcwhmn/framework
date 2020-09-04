package com.yangzhou.domain;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StringPair {
  private final String key;
  private final Object value;
  
  public static Map<String, Object> toMap(StringPair... pairs) {
    Map<String, Object> map = new HashMap<>();
    for(StringPair pair: pairs) {
      map.put(pair.key, pair.value);
    }
    return map;
  }
  
  public static Map<String, String> toStringMap(StringPair... pairs) {
    Map<String, String> map = new HashMap<>();
    for(StringPair pair: pairs) {
      map.put(pair.key, (String) pair.value);
    }
    return map;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static QueryWrapper toQueryWrapper(StringPair... pairs) {
    QueryWrapper queryWrapper = new QueryWrapper();
    
    for(StringPair pair: pairs) {
      queryWrapper.eq(pair.key, pair.value);
    }
    return queryWrapper;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static QueryWrapper queryWrapper(String[]... pairs) {
    QueryWrapper queryWrapper = new QueryWrapper();
    for(String[] pair: pairs) {
      queryWrapper.eq(pair[0], pair[1]);
    }
    return queryWrapper;
  }
}
  
