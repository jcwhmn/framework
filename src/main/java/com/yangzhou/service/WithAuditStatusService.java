package com.yangzhou.service;

import org.springframework.transaction.annotation.Transactional;
import com.yangzhou.domain.WithAuditStatus;
import com.yangzhou.enums.AuditStatusEnum;
import com.yangzhou.errors.BadRequestException;

public interface WithAuditStatusService<T extends WithAuditStatus> extends AssertService {
  String getType();
  Class getTClass();

  boolean updateById(T entity);

  default boolean sendAudit(long id) {
    final T                      t          = (T) assertExistById(id, getType(), getTClass());
    final AuditStatusEnum statusEnum = t.auditStatusEnum();
    if (statusEnum == AuditStatusEnum.Normal || statusEnum == AuditStatusEnum.Auditing)
      throw new BadRequestException(0, String.format("只有新建或认证失败状态的%s才能被送审！", getType()));

    t.auditStatusEnum(AuditStatusEnum.Auditing);
    return updateById(t);
  }

  @Transactional default boolean auditPass(long id) {
    final T                      t          = (T) assertExistById(id, getType(), getTClass());
    final AuditStatusEnum statusEnum = t.auditStatusEnum();
    if (statusEnum != AuditStatusEnum.Auditing)
      throw new BadRequestException(0, String.format("只能通过认证中状态%s！", getType()));

    t.auditStatusEnum(AuditStatusEnum.Normal);
    return updateById(t);
  }

  @Transactional default boolean auditFail(long id) {
    final T               t          = (T) assertExistById(id, getType(), getTClass());
    final AuditStatusEnum statusEnum = t.auditStatusEnum();
    if (statusEnum != AuditStatusEnum.Auditing)
      throw new BadRequestException(0, String.format("只能拒绝认证中状态%s！", getType()));

    t.auditStatusEnum(AuditStatusEnum.AuditFail);
    return updateById(t);
  }

}
