package com.yangzhou.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yangzhou.config.JacksonConfiguration;
import com.yangzhou.errors.JacksonConvertException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("jsonService")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JacksonJsonService implements JsonService {
  private static final ObjectMapper mapperObjectWithClassInfo;
  private static final ObjectMapper mapperObject;

  static {
    mapperObject              = JacksonConfiguration.createMapperObject();
    mapperObjectWithClassInfo = JacksonConfiguration.createMapperObjectWithClassInfo();
  }

  private final String JAVALANGPACKAGE =  "java.lang";
  @Override
  public <T> String toJson(T obj) {
    if (obj == null)
      return null;


    try {
      final String className = obj.getClass().getName();
      if(obj.getClass().getName().startsWith(JAVALANGPACKAGE)) return mapperObjectWithClassInfo.writeValueAsString(new Wrapper(obj));
      else
        return mapperObjectWithClassInfo.writeValueAsString(obj);
    } catch (final JsonProcessingException e) {
      log.error("序列化出错。{}", obj.toString());
      log.error(e.getStackTrace().toString());
      throw new JacksonConvertException("序列化对象时出错！");
    }
  }

  @Override
  public <T> T toBean(byte[] json) {
    if (json == null || json.length == 0)
      return null;

    return toBean( new String(json));
  }

  @Override
  public <T> T toBean(String json) {
    if (json == null)
      return null;

    try {
      final JsonNode node      = mapperObjectWithClassInfo.readTree(json);
      String className = node.getClass().getSimpleName();
      switch (className) {
      case "ArrayNode":
        final Iterator<JsonNode> nodes = node.iterator();
        className = nodes.next().asText();
        break;
      case "ObjectNode":
        className = node.findValue("@class").asText();
        break;
      default:
        log.error("反序列化出错。{}", json);
        throw new JacksonConvertException(String.format("反序列化时出错，%s为不支持的对象类型", className));
      }
      if (className == null) {
        log.error("反序列化出错。{}", json);
        throw new JacksonConvertException("反序列化时出错，找不到有效的对象信息！");
      }
      if(Wrapper.class.getName().equals(className)) {
        final Wrapper wrapper = mapperObjectWithClassInfo.readValue(json, Wrapper.class);
        return (T) Class.forName(wrapper.getClassName()).cast(wrapper.getField());
      }	else
        return (T) mapperObjectWithClassInfo.readValue(json, Class.forName(className));
    } catch (final Exception e) {
      e.printStackTrace();
      log.error("反序列化出错。{}", json);
      log.error(e.getMessage());
      throw new JacksonConvertException("反序列化时出错，无效的字符串！");
    }
  }

  @Override
  public <T> T toBean(String json, Class<T> clz) {
    if (json == null)
      return null;

    try {
      return mapperObject.readValue(json, clz);
    } catch (final Exception e) {
      log.error("反序列化出错。{}", json);
      log.error(e.getStackTrace().toString());
      throw new JacksonConvertException("反序列化时出错，无效的字符串！");
    }
  }

  @Override
  public <T> Map<String, T> toMap(String json, Class<T> clazz) {
    return to3ClassObject(json, HashMap.class, String.class, clazz);
  }

  @Override
  public <T> Map<String, List<T>> toListMap(String json, Class<T> clazz) {
    final Map<String, Object>  listsMap = to3ClassObject(json, HashMap.class, String.class, Object.class);
    final Map<String, List<T>> result   = new HashMap<>();
    listsMap.forEach((key, obj) -> {
      result.put(key, toList(toJsonWithoutClassInfo(obj), clazz));
    });

    return result;
  }

  @Override
  public <U, V> Map<U, V> toMap(String json, Class<U> clazzU, Class<V> clazzV) {
    return to3ClassObject(json, HashMap.class, clazzU, clazzV);
  }

  @Override
  public <T> List<T> toList(String json, Class<T> clazz) {
    return to2ClassObject(json, ArrayList.class, clazz);
  }

  @Override
  public <T> Set<T> toSet(String json, Class<T> clazz) {
    return to2ClassObject(json, HashSet.class, clazz);
  }

  @Override
  public <U, V> U to2ClassObject(String json, Class<U> clazzU, Class<V> clazzV) {
    if (json == null || clazzU == null || clazzV == null)
      return null;

    final JavaType javaType = mapperObject.getTypeFactory().constructParametricType(clazzU, clazzV);
    try {
      return mapperObject.readValue(json, javaType);
    } catch (final Exception e) {
      log.error("反序列化到set出错。{}, {}, {}", json, clazzU.getName(), clazzV.getName());
      log.error(e.getStackTrace().toString());
      throw new JacksonConvertException("反序列化时出错，无效的字符串！");
    }
  }

  @Override
  public <U, V, W> U to3ClassObject(String json, Class<U> clazzU, Class<V> clazzV, Class<W> clazzW) {
    if (json == null || clazzU == null || clazzV == null || clazzW == null)
      return null;

    final JavaType javaType = mapperObject.getTypeFactory().constructParametricType(clazzU, clazzV, clazzW);
    try {
      return mapperObject.readValue(json, javaType);
    } catch (final Exception e) {
      log.error("反序列化到set出错。{}, {}, {}, {}", json, clazzU.getName(), clazzV.getName(), clazzW.getName());
      log.error(e.getStackTrace().toString());
      throw new JacksonConvertException("反序列化时出错，无效的字符串！");
    }
  }

  @Override
  public <U, V, W, X> U to4ClassObject(String json, Class<U> clazzU, Class<V> clazzV, Class<W> clazzW, Class<X> clazzX) {
    if (json == null || clazzU == null || clazzV == null || clazzW == null || clazzX == null)
      return null;

    final JavaType javaType = mapperObject.getTypeFactory().constructParametricType(clazzU, clazzV, clazzW, clazzX);
    try {
      return mapperObject.readValue(json, javaType);
    } catch (final Exception e) {
      log.error("反序列化到set出错。{}, {}, {}, {}", json, clazzU.getName(), clazzV.getName(), clazzW.getName());
      log.error(e.getStackTrace().toString());
      throw new JacksonConvertException("反序列化时出错，无效的字符串！");
    }
  }

  @Override
  public <T> T toBean(Object object, Class<T> clz) {
    if (object == null || clz == null)
      return null;

    try {
      return mapperObject.convertValue(object, clz);
    } catch (final Exception e) {
      log.error("反序列化出错。{}, 类型：{}", object.toString(), clz.getName());
      log.error(e.getStackTrace().toString());
      throw new JacksonConvertException("反序列化时出错，无效的Map！");
    }
  }

  @Override public <T> String toJsonWithoutClassInfo(T obj) {
    if (obj == null)
      return null;

    try {
      return mapperObject.writeValueAsString(obj);
    } catch (final JsonProcessingException e) {
      log.error("序列化出错。{}", obj.toString());
      log.error(e.getStackTrace().toString());
      throw new Error("JsonService: Object to String mapper error");
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  private static class Wrapper<T>{
    private T field;
    private String className;
    public Wrapper(T field) {
      this.field = field;
      className = field.getClass().getName();
    }
  }

  @Override public String formatJsonString(String json) {
    // TODO Auto-generated method stub
    try {
      final JsonNode node = mapperObject.readTree(json);
      return mapperObject.writerWithDefaultPrettyPrinter().writeValueAsString(node);
    } catch (final IOException e) {
      log.error("反序列化出错。{}", json);
      log.error(e.getStackTrace().toString());
      throw new Error("JsonService: Object to String mapper error");
    }
  }
}
