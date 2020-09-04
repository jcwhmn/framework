package com.yangzhou.enums;

public enum AuditStatusEnum {
  // NewCreated(1, "新建"),
  Auditing(2, "审核中"),
  AuditFail(3, "审核未通过"),
  Normal(10, "正常");

  public final int    value;
  public final String status;

  public static AuditStatusEnum valueOfStatus(String status) {
    for (final AuditStatusEnum statusEnum : AuditStatusEnum.values()) {
      if (statusEnum.status.equals(status))
        return statusEnum;
    }
    return null;
  }

  public static AuditStatusEnum initStatusEnum() {
    return Auditing;
  }
  AuditStatusEnum(int value, String stage) {
    this.value = value;
    status     = stage;
  }
}
