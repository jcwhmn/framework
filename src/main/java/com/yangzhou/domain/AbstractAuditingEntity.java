package com.yangzhou.domain;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 所有需要记录创建者、修改者等跟踪记录的实体需要继承该类。 继承该类的实体在建表时，需要加上：
 *
 * insert_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
 * insert_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP, `update_by`
 * varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
 * update_at` datetime(0) NULL DEFAULT NULL,
 *
 * 这4个字段
 *
 * @author 姜传伟
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractAuditingEntity extends BaseEntityWithID {
  /** 增加记录时，自动插入登录用户login。mybatis plus自动操作 */
  @TableField(fill = FieldFill.INSERT)
  @ApiModelProperty(value = "系统在对象创建时自动插入，标识对象的创建者",example = "操作人")
  protected String insertBy;

  /** 增加记录时，自动插入当前时间。mybatis plus自动操作 */
  @TableField(fill = FieldFill.INSERT)
  @ApiModelProperty(value = "系统在对象创建时自动插入，标识对象的创建时间",example = "2020-03-07 11:22:22")
  protected LocalDateTime insertAt;

  /** 修改记录时，自动插入登录用户login。mybatis plus自动操作 */
  @TableField(fill = FieldFill.UPDATE)
  @ApiModelProperty(value = "系统在对象修改时自动插入，标识对象的修改者",example = "修改人")
  protected String updateBy;

  /** 修改记录时，自动插入当前时间。mybatis plus自动操作 */
  @TableField(fill = FieldFill.UPDATE)
  @ApiModelProperty(value = "系统在对象修改时自动插入，标识对象的修改时间",example = "2020-03-07 11:22:22")
  protected LocalDateTime updateAt;
}
