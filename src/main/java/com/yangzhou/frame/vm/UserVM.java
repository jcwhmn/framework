package com.yangzhou.frame.vm;

import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import com.yangzhou.config.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * View model。用于传输可以在页面上显示的用户信息
 */
@Getter
@Setter
@ToString(exclude = { "authorities" })
@EqualsAndHashCode(exclude = { "authorities" })
@ApiModel(value = "用户对象VM")
public class UserVM {

  protected Long        id;

  @NotNull @Pattern(regexp = Constants.LOGIN_REGEX) 
  @Size(min = 5, max = 50) 
  @ApiModelProperty(value = "用户登录名,最短5位，最长50位",example = "user111")
  private String login;

  @Size(min = 2,max = 50) 
  @NotNull 
  @ApiModelProperty(value = "用户姓名。用户登录后显示该姓名",example = "张三")
  private String name;

  @ApiModelProperty(value = "用户状态。缺省值为 正常。状态为正常时，才能够登录系统",example = "正常")
  protected String      status;
  @ApiModelProperty(value = "用户授权。不用填制")
  protected Set<String> authorities = new HashSet<>();
}