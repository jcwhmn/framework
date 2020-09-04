package com.yangzhou.frame.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yangzhou.config.Constants;
import com.yangzhou.domain.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A user.只包含用户的登录信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends AbstractAuditingEntity implements Serializable {
  private static final long serialVersionUID = -2816517320093516089L;
  @TableId(type = IdType.INPUT)
  @NotNull(groups = { Update.class })
  protected Long id;
  @NotNull
  @Pattern(regexp = Constants.LOGIN_REGEX)
  @Size(min = 1, max = 50)
  private String login;

  @JsonIgnore
  @NotNull
  @Size(min = 60, max = 60)
  private String password;

  @Size(min = 3, max = 50)
  private String name;

  @TableField(exist = false)
  private Set<Authority> authorities = new HashSet<>();

  @JsonIgnore
  @TableLogic
  private Integer isDelete;
  private String status;
}
