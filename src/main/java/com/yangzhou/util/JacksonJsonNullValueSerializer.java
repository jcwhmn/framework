package com.yangzhou.util;

import java.util.Arrays;
import java.util.List;
import org.springframework.security.util.FieldUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JacksonJsonNullValueSerializer extends JsonSerializer<Object>{
  @Override public void serialize(Object value, JsonGenerator jg, SerializerProvider sp) {
    Object obj = jg.getOutputContext().getCurrentValue();
    String field = jg.getOutputContext().getCurrentName();
      try {
        String className;
        className = FieldUtils.getField(obj.getClass(), field).getType().getSimpleName();
        if(isPrimitive(className)) {
          jg.writeString("");
        }else {
          Object obj2 = obj.getClass().getDeclaredField(field).getType().newInstance();
          jg.writeObject(obj2);
        }
      } catch (NoSuchFieldException e1) {
        //skip inherent fields
      } catch (Exception e) {
        throw new RuntimeException("处理null字段时出错！");
      }
  }

  private boolean isPrimitive(String className) {
    List<String> primitiveClassnameSet = Arrays.asList("String", "Byte", "Short", "Integer", "Long", "Float", "Double", "Character", "Boolean", "Void");
    return primitiveClassnameSet.contains(className);
  }
}
