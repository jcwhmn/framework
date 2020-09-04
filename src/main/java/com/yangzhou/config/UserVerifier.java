package com.yangzhou.config;

import java.util.regex.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import com.yangzhou.errors.PasswordVerificationException;

/**
 * 使用用户自定义正则表达式进行用户密码复杂度验证
 * @author 姜传伟
 *
 *在application.yml配置文件中增加配置。userverify
 */
@Configuration
@EnableConfigurationProperties(UserVerifier.class)
@ConfigurationProperties(prefix = "userverify",ignoreUnknownFields = true)
public class UserVerifier {
  /**  配置项password_pattern   正则表达式。如^.{8,}$保证8个字符 */
  private String passwordPattern;
  /** 配置项password_invalid_message 配置密码复杂度验证错误时的错误消息。若不设置，消息为无效的密码   */
  private String passwordInvalidMessage;

  private String getPasswordInvalidMessage() {
    if(StringUtils.isEmpty(passwordInvalidMessage))
      return "无效的密码!";
    else
      return passwordInvalidMessage;
  }

  public void verifyPassword(String password) {
    if (!StringUtils.isEmpty(passwordPattern)) {
      if (!Pattern.matches(passwordPattern, password)) throw new PasswordVerificationException(getPasswordInvalidMessage());
    }
  }
}
