package com.yangzhou.frame.vm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * View model。用于传输可以在页面上显示的用户信息
 */
@Getter
@Setter
@ToString(exclude = {"password","authorities" })
@ApiModel(value = "用户注册对象VM")
public class CreateUserVM extends UserVM {
  @NotNull @Size(min = 6, max = 20) 
  @ApiModelProperty(value = "密码,最短6位，最长20位",example = "123456")
  private String password;
}