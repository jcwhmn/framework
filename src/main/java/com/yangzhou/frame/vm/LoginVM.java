package com.yangzhou.frame.vm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * View Model。用于登录页面上传登录信息。
 */
@Getter
@Setter
@ToString(exclude = { "password" })
@NoArgsConstructor
@ApiModel(value = "用户登录输入信息对象")
public class LoginVM {
  @NotNull
  @Size(min = 5, max = 50)
  @ApiModelProperty(value = "用户登录名,最短5位，最长50位")
  private String login;
  @NotNull
  @ApiModelProperty(value = "登录密码")
  private String password;
  @ApiModelProperty(value = "是否长期保存该用户登录状态")
  private Boolean rememberMe;

  public LoginVM(String login, String password) {
    this.login = login;
    this.password = password;
  }
}
