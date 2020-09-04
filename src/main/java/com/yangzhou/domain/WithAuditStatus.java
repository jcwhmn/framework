package com.yangzhou.domain;

import com.yangzhou.enums.AuditStatusEnum;

public interface WithAuditStatus {
  void setAuditStatus(String auditStatus);
  String getAuditStatus();

  default AuditStatusEnum auditStatusEnum() {
    return AuditStatusEnum.valueOfStatus(getAuditStatus());
  }

  default void auditStatusEnum(AuditStatusEnum statusEnum) {
    setAuditStatus(statusEnum.status);
  }
}
