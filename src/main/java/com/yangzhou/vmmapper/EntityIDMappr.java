package com.yangzhou.vmmapper;

import com.yangzhou.domain.BaseEntityWithID;
import com.yangzhou.service.EntityExtensionManager;

public class EntityIDMappr<T extends BaseEntityWithID> {
  Class<T> clz;

  public EntityIDMappr(Class<T> clz) {
    this.clz = clz;
  }
  public long toLong(T entity) {
    return entity.getId();
  }

  public T map(long id) {
    return new EntityExtensionManager().getById(id, clz);
  }
}
