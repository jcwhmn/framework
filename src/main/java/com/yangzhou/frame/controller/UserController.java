package com.yangzhou.frame.controller;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yangzhou.aop.logging.SysLogAnnotation;
import com.yangzhou.errors.UserNotFoundException;
import com.yangzhou.frame.service.UserService;
import com.yangzhou.frame.vm.CreateUserVM;
import com.yangzhou.frame.vm.PasswordChangeVM;
import com.yangzhou.frame.vm.UserVM;
import com.yangzhou.util.MsgInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Api(tags = "框架>用户管理")
public class UserController {
  private final UserService userService;

  /***
   * 用户创建
   *
   * @param userVM
   * @return
   * @throws Exception
   */
  @PostMapping("/users/register")
  @SysLogAnnotation(value = "用户创建")
  @ApiOperation("用户注册。生成用户对象")
  public ResponseEntity<MsgInfo<Boolean>> create(@Valid @RequestBody CreateUserVM userVM) throws Exception {
    userService.createUser(userVM);
    return MsgInfo.ok("用户创建成功!", true);
  }

  /**
   * 根据登录名删除用户
   *
   * @param login
   * @return
   */
  @DeleteMapping("/users/{login}")
  @SysLogAnnotation(value = "删除用户")
  @ApiOperation("删除用户对象")
  public ResponseEntity<MsgInfo<Boolean>> delete(@PathVariable("login") String login) {
    userService.delete(login);
    return MsgInfo.ok("删除用户成功!", true);
  }

  /***
   * 根据用户id删除用户
   *
   * @param id
   * @return
   */
  @DeleteMapping("/users/{id}/byId")
  @SysLogAnnotation(value = "删除用户")
  @ApiOperation("删除用户对象")
  public ResponseEntity<MsgInfo<Boolean>> delete(@PathVariable("id") long id) {
    userService.delete(id);
    return MsgInfo.ok("删除用户成功!", true);
  }

  /***
   * 修改用户状态
   *
   * @param id
   * @param status
   * @return
   */
  @PostMapping("/users/{id}/{status}/status")
  @SysLogAnnotation(value = "改变用户状态")
  @ApiOperation("改变用户状态，用于冻结或解封用户")
  public ResponseEntity<MsgInfo<Boolean>> updateStatus(@PathVariable("id") long id,
        @PathVariable("status") String status) {
    userService.updateStatus(id, status);
    return MsgInfo.ok("改变用户状态成功!", true);
  }

  /***
   * 密码重置
   *
   * @param passwordChangeVM
   * @return
   */
  @PostMapping("/users/resetPassword")
  @SysLogAnnotation(value = "密码重置")
  @ApiOperation("重置用户登录密码，这里如果newPassword为空，则重置为缺省密码。")
  public ResponseEntity<MsgInfo<Boolean>> resetPassword(@RequestBody PasswordChangeVM passwordChangeVM) {
    userService.resetPassword(passwordChangeVM);
    return MsgInfo.ok("密码修改成功！", true);
  }

  /***
   *密码修改
   * @param passwordChangeVM
   * @return
   */
  @PostMapping("/users/updatePassword")
  @SysLogAnnotation(value = "密码修改")
  @ApiOperation("改变用户登录密码。")
  public ResponseEntity<MsgInfo<Boolean>> updatePassword(@RequestBody PasswordChangeVM passwordChangeVM){
    userService.updatePassword(passwordChangeVM);
    return MsgInfo.ok("密码修改成功! ", true);
  }

  /***
   * 密码重置
   *
   * @param currentPassword
   * @param newPassword
   * @return
   */
  @PostMapping("/users/{currentPassword}/{newPassword}/updatePassword")
  @SysLogAnnotation(value = "密码重置")
  @ApiOperation("改变用户登录密码。")
  public ResponseEntity<MsgInfo<Boolean>> updatePassword(@PathVariable("currentPassword") String currentPassword,
        @PathVariable("newPassword") String newPassword) {
    userService.updatePassword(currentPassword, newPassword);
    return MsgInfo.ok("密码修改成功！", true);
  }

  /**
   * 用户更新
   *
   * @param userVM
   * @return
   */
  @PostMapping("/users")
  @SysLogAnnotation(value = "用户更新")
  @ApiOperation("改变用户信息。很少用")
  public ResponseEntity<MsgInfo<Boolean>> update(@RequestBody UserVM userVM) {
    if (userVM.getId() != null) {
      userService.updateUser(userVM);
    } else throw new UserNotFoundException();
    return MsgInfo.ok("User update successfully", true);
  }

  /***
   * 查看用户详情
   *
   * @param login
   * @return
   */
  @GetMapping("/users/{login}")
  @SysLogAnnotation(
        value = "获取用户信息") @ApiOperation("按登录名获取用户信息")
  public ResponseEntity<MsgInfo<UserVM>> getUser(@PathVariable String login) {
    final UserVM userVM = userService.getUserWithAuthoritiesByLogin(login);
    return MsgInfo.ok("查询成功", userVM);
  }

  /***
   * 查看用户详情
   *
   * @param id
   * @return
   */
  @GetMapping("/users/{id}/byId")
  @ApiOperation("按ID获取用户信息")
  public ResponseEntity<MsgInfo<UserVM>> getUserById(@PathVariable long id) {
    final UserVM user = userService.getUserWithAuthoritiesById(id);
    return MsgInfo.ok("查询成功", user);
  }

  /**
   * 获取当前用户信息
   *
   * @return
   */
  @GetMapping("/users/info")
  @ApiOperation("获取当前登录用户信息")
  public ResponseEntity<MsgInfo<UserVM>> getCurrentUserInfo() {
    final UserVM userVM = userService.getUserWithAuthorities();
    return MsgInfo.ok(userVM);
  }
}
