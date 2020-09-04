package com.yangzhou.vmmapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.yangzhou.domain.LayeredEntity;
import com.yangzhou.vm.LayeredEntityVM;

public interface LayeredVMMapper<TVM extends LayeredEntityVM<T>, T extends LayeredEntity> extends BaseVMMapper<TVM, T> {
  default List<TVM> toTreeVM(List<T> childrenList) {
    final List<TVM> result = new ArrayList<>();
    if (childrenList == null || childrenList.size() == 0)
      return result;

    final int              layerLevel       = childrenList.get(0).layerLevel();
    final Map<String, TVM> layerLocationMap = new HashMap<>();
    childrenList.forEach(child -> {
      final TVM childVm = toVm(child);
      layerLocationMap.put(child.getLayer(), childVm);
      if (child.layerLevel() - layerLevel > 0) {
        final String parentLayer = child.parent();
        final TVM    parentVm    = layerLocationMap.get(parentLayer);
        parentVm.add(childVm);
      } else {
        result.add(childVm);
      }
    });
    return result;
  }
}
