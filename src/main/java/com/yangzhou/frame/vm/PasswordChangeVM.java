package com.yangzhou.frame.vm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * View model. 用于重置用户密码
 */
@Getter
@Setter
@NoArgsConstructor
@ApiModel(value = "改变密码VM")
public class PasswordChangeVM {
  @ApiModelProperty(value = "用户登录名",example = "user111")
  private String   login;
  @ApiModelProperty(value = "原密码")
  private String currentPassword;
  @ApiModelProperty(value = "新密码")
  private String newPassword;

  public PasswordChangeVM(String currentPassword, String newPassword) {
    this.currentPassword = currentPassword;
    this.newPassword     = newPassword;
  }
}
