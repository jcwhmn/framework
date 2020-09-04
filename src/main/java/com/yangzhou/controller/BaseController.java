package com.yangzhou.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yangzhou.domain.BaseEntityWithID;
import com.yangzhou.util.MsgInfo;
import io.swagger.annotations.ApiOperation;

public interface BaseController<T extends BaseEntityWithID> {
  IService service();

  @ApiOperation("按ID获取实体对象")
  @GetMapping("/{id}") default ResponseEntity<MsgInfo<T>> get(@PathVariable("id") long id) {
    final T t = (T) service().getById(id);
    if (t == null)
      return MsgInfo.fail(null);
    else return MsgInfo.ok(t);
  }

  @ApiOperation("获取所有实体列表")
  @GetMapping("") default ResponseEntity<MsgInfo<List<T>>> list() {
    return MsgInfo.ok(service().list());
  }

  @ApiOperation("完整修改实体对象")
  @PutMapping("") default ResponseEntity<MsgInfo<Boolean>> update(@RequestBody T entity) {
    return MsgInfo.ok(service().updateById(entity));
  }

  @ApiOperation("新增实体对象")
  @PostMapping("") default ResponseEntity<MsgInfo<Boolean>> insert(@RequestBody T entity) {
    return MsgInfo.ok(service().save(entity));
  }

  @ApiOperation("按ID删除实体对象")
  @DeleteMapping("/{id}") default ResponseEntity<MsgInfo<Boolean>> delete(@PathVariable("id") long id) {
    service().removeById(id);
    return MsgInfo.ok(true);
  }
}
