package com.yangzhou.frame.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yangzhou.frame.domain.ExtensionType;
import com.yangzhou.frame.mapper.ExtensionTypeMapper;
import com.yangzhou.service.EntityExtensionManager;
import lombok.Setter;

@Service("extensionService")
public class ExtensionTypeServiceImpl implements ExtensionTypeService {
  @Autowired ExtensionTypeMapper extensionTypeMapper;
  private Map<String, ExtensionType> typeMap = null;
  private final Map<String, TypeHandler> typeHandlerMap = new HashMap<>();

  @Override
  public <T> T parse(String className, String name, String value) {
    return typeHandler(getKey(className, name)).getValue(value);
  }

  private ExtensionType getExtensionType(String type) {
    if (typeMap == null) {
      final List<ExtensionType> extensionTypes = extensionTypeMapper.selectList(null);
      typeMap = new HashMap<>();
      extensionTypes.forEach(extensionType -> typeMap.put(getKey(extensionType), extensionType));
    }
    return typeMap.get(type);
  }

  private String getKey(ExtensionType extensionType) {
    return getKey(extensionType.getExtensionClassName(), extensionType.getFieldName());
  }

  private String getKey(String className, String fieldName) {
    return className + " " + fieldName;
  }

  private TypeHandler typeHandler(String key) {
    if (typeHandlerMap.containsKey(key))
      return typeHandlerMap.get(key);

    final ExtensionType extensionType = getExtensionType(key);
    TypeHandler typeHandler = null;

    final String className = this.getClass().getPackage().getName() + "." + extensionType.getFieldType() + "Handler";
    try {
      typeHandler = (TypeHandler) Class.forName(className).newInstance();
      typeHandler.setExtensionType(extensionType);
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    typeHandlerMap.put(key, typeHandler);
    return typeHandler;
  }

}

@Setter
abstract class TypeHandler {
  protected ExtensionType extensionType;

  public abstract <T> T getValue(String value);
}

class LongHandler extends TypeHandler {
  @Override public <T> T getValue(String value) {
    return (T) Long.valueOf(value);
  }
}

class DoubleHandler extends TypeHandler {
  @Override public <T> T getValue(String value) {
    return (T) Double.valueOf(value);
  }
}

class BooleanHandler extends TypeHandler {
  @Override public <T> T getValue(String value) {
    return (T) Boolean.valueOf(value);
  }
}

class StringHandler extends TypeHandler {
  @Override public <T> T getValue(String value) {
    return (T) value;
  }
}

class ObjectHandler extends TypeHandler {
  EntityExtensionManager extensionManager = new EntityExtensionManager();

  @Override public <T> T getValue(String value) {
    final long   id          = Long.parseLong(value);
    final String serviceName = extensionType.getFieldClassName();
    return extensionManager.getById(id, serviceName);
  }
}
