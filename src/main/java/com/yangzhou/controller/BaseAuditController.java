package com.yangzhou.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.yangzhou.domain.WithAuditStatus;
import com.yangzhou.service.WithAuditStatusService;
import com.yangzhou.util.MsgInfo;
import io.swagger.annotations.ApiOperation;

public interface BaseAuditController<T extends WithAuditStatus> {
  WithAuditStatusService<T> withAuditStatusService();

  @ApiOperation("审核 》送审") @PatchMapping("/{id}/sendAudit")
  default ResponseEntity<MsgInfo<Boolean>> sendAudit(@PathVariable("id") long id) {
    return MsgInfo.ok(withAuditStatusService().sendAudit(id));
  }

  @ApiOperation("审核 》审核通过") @PatchMapping("/{id}/auditPass")
  default ResponseEntity<MsgInfo<Boolean>> auditPass(@PathVariable("id") long id) {
    return MsgInfo.ok(withAuditStatusService().auditPass(id));
  }

  @ApiOperation("审核 》审核不通过") @PatchMapping("/{id}/auditFail")
  default ResponseEntity<MsgInfo<Boolean>> auditFail(@PathVariable("id") long id) {
    return MsgInfo.ok(withAuditStatusService().auditFail(id));
  }
}
