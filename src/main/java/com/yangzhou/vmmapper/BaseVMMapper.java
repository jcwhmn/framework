package com.yangzhou.vmmapper;

import java.util.List;
import java.util.Set;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface BaseVMMapper<VM, Entity> {
  Entity toDomain(VM vm);
  VM toVm(Entity domain);

  List<Entity> toDomains(List<VM> dtoList);
  List<VM> toVms(List<Entity> domainList);

  Set<Entity> toDomains(Set<VM> dtoList);
  Set<VM> toVms(Set<Entity> domainList);

  default Page<VM> toVms(Page<Entity> source) {
    final Page<VM> dest = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
    dest.setRecords(toVms(source.getRecords()));
    return dest;
  }

  default Page<Entity> toDomains(Page<VM> source) {
    final Page<Entity> dest = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
    dest.setRecords(toDomains(source.getRecords()));
    return dest;
  }

}
