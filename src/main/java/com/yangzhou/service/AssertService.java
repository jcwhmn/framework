package com.yangzhou.service;

import java.util.Collection;
import java.util.List;
import org.springframework.util.CollectionUtils;
import com.yangzhou.errors.EmptyCollectionException;
import com.yangzhou.errors.ObjectExistedAlreadyException;
import com.yangzhou.errors.ObjectNotFoundException;

public interface AssertService {
  default ExtensionManager manager() {
    return EntityExtensionManager.instance();
  }

  default <W> W assertExistById(long id, Class<W> clz) {
    return assertExistById(id, clz.getSimpleName(), clz);
  }

  default <W> W assertExistById(long id, String type, Class<W> clz) {
    final W w = manager().getById(id, clz);
    if (w == null)
      throw new ObjectNotFoundException(id, type);
    return w;
  }

  default <W> void assertNotExistById(long id, Class<W> clz) {
    assertNotExistById(id, clz.getSimpleName(), clz);
  }

  default <W> void assertNotExistById(long id, String type, Class<W> clz) {
    final W w = manager().getById(id, clz);
    if (w != null)
      throw new ObjectExistedAlreadyException(id, type);
  }

  default <W> W getById(Long id, Class<W> clz) {
    return manager().getById(id, clz);
  }

  default <W> List<W> assertNotEmptyList(Class<W> clz) {
    final List<W> list = manager().list(clz);

    if (CollectionUtils.isEmpty(list))
      throw new EmptyCollectionException(clz.getSimpleName());

    return list;
  }

  default <W> List<W> list(Class<W> clz) {
    return manager().list(clz);
  }

  default <W> W notNull(W obj, String msg) {
    if (obj == null)
      throw new ObjectNotFoundException(msg);
    return obj;
  }

  default boolean isBlank(Collection list) {
    if (list == null || list.size() == 0)
      return true;
    return false;
  }

}
