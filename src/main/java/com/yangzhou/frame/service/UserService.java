package com.yangzhou.frame.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yangzhou.config.Constants;
import com.yangzhou.config.UserVerifier;
import com.yangzhou.enums.StatusCode;
import com.yangzhou.errors.BadRequestException;
import com.yangzhou.errors.LoginAlreadyUsedException;
import com.yangzhou.errors.ObjectNotFoundException;
import com.yangzhou.errors.UserNotFoundException;
import com.yangzhou.frame.domain.Authority;
import com.yangzhou.frame.domain.User;
import com.yangzhou.frame.mapper.AuthorityMapper;
import com.yangzhou.frame.mapper.UserMapper;
import com.yangzhou.frame.vm.CreateUserVM;
import com.yangzhou.frame.vm.PasswordChangeVM;
import com.yangzhou.frame.vm.UserVM;
import com.yangzhou.frame.vmmapper.UserVMMapper;
import com.yangzhou.security.AuthoritiesConstants;
import com.yangzhou.security.SecurityUtils;
import com.yangzhou.service.BaseServiceImpl;
import com.yangzhou.util.SetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing users.
 */
@Slf4j
@Service
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class UserService extends BaseServiceImpl<UserMapper, User> {
  protected final static String CACHENAME = "user";
  protected final static String VMCACHENAME = "userVM";
  @Autowired private UserVMMapper userVMMapper;

  @Autowired
  private UserMapper                 userMapper;
  @Autowired
  private AuthorityMapper            authorityMapper;
  @Autowired
  private PasswordEncoder            passwordEncoder;
  @Autowired
  private UserVerifier               userVerifier;

  /**
   * 创建用户
   *
   * @param userVM
   * @return 创建后的用户
   */
  @Transactional
  //@CachePut(value = VMCACHENAME, key = "#userVM.getLogin().toLowerCase()")
  public UserVM createUser(CreateUserVM userVM) {
    final String login = userVM.getLogin().trim().toLowerCase();

    confirmLoginNotExisted(login);
    userVM.setLogin(login);

    final User newUser = userVMMapper.toDomain(userVM);

    setPassword(userVM.getPassword(), newUser);
    newUser.setStatus("正常");
    final int count = userMapper.insert(newUser);
    if (count != 1)
      throw new BadRequestException(StatusCode.fail.value, "不能创建用户！");

    addDefaultAuthority(newUser);
    log.debug("User {} created!", newUser);

    return userVMMapper.toVm(newUser);
  }

  private void setPassword(String password, final User newUser) {
    userVerifier.verifyPassword(password);
    final String encodePassword = passwordEncoder.encode(password);
    newUser.setPassword(encodePassword);
  }

  private void setPassword(CreateUserVM userVM, final User newUser) {
    String password = userVM.getPassword();
    if (StringUtils.isEmpty(password)) {
      password = Constants.INITIAL_PASSWORD;
    }
    final String encodePassword = passwordEncoder.encode(password);
    newUser.setPassword(encodePassword);
  }

  private void addDefaultAuthority(final User newUser) {
    final Set<Authority> authorities = new HashSet<>();
    final Authority authority = authorityMapper.findOneByName(AuthoritiesConstants.USER);
    if (authority != null) {
      authorities.add(authority);
      userMapper.insertUserAuthority(newUser.getLogin(), AuthoritiesConstants.USER);
    }
    newUser.setAuthorities(authorities);
  }

  private void confirmLoginNotExisted(String login) {
    if (null != getUserByLoginIntern(login))
      throw new LoginAlreadyUsedException();
  }

  private void confirmLoginIsExisted(String login) {
    if (null == getUserByLoginIntern(login))
      throw new ObjectNotFoundException("找不到用户");
  }

  private User getUserByLoginIntern(String login) {
    return userMapper.selectOne(new QueryWrapper<User>().eq("login", login));
  }

  /**
   * 获取用户信息
   *
   * @param login
   * @return
   */
  public User getUserByLogin(String login) {
    final User user = getUserByLoginIntern(login);
    if (user == null)
      throw new ObjectNotFoundException("找不到用户");
    return user;
  }

  public User getUserById(long id) {
    final User user = userMapper.selectById(id);
    if (user == null)
      throw new ObjectNotFoundException("找不到用户");
    return user;
  }

  /**
   * 按用户login删除用户
   *
   * @param login
   * @return boolean
   */
  public boolean delete(String login) {
    confirmLoginIsExisted(login);
    return deleteUser(login);
  }

  /**
   * 按用户id删除用户
   *
   * @param id
   * @return boolean
   */
  public boolean delete(long id) {
    final User user = getUserById(id);
    return deleteUser(user.getLogin());
  }

  /**
   * 按用户login删除用户
   *
   * @param login
   * @return
   */
  @Transactional
  //@CacheEvict(value = VMCACHENAME, key = "#login")
  private boolean deleteUser(String login) {
    userMapper.deleteUserAuthorityByUserLogin(login);
    final int count = userMapper.delete(new UpdateWrapper<User>().eq("login", login));
    return count == 1;
  }

  /**
   * Update all information for a specific user, and return the modified user.
   *
   * @param userVM user to update
   * @return updated user
   */
  @Transactional
  public boolean updateUser(UserVM userVM) {
    if (null == userVM.getId())
      throw new UserNotFoundException();

    final String login = userVM.getLogin().trim().toLowerCase();

    final User user = getUserById(userVM.getId());
    if (!user.getLogin().equals(login)) {
      confirmLoginNotExisted(login);
    }
    userVM.setLogin(login);

    final int count = userMapper.updateById(userVMMapper.toDomain(userVM));
    if (count == 1) {
      updateUserAuthorities(userVM);

      cacheService.set(VMCACHENAME, login, userVM);
    }
    return count == 1;
  }

  private void updateUserAuthorities(UserVM userVM) {
    final String login = userVM.getLogin();
    final SetUtil.AddDelete<String> addDelete = SetUtil.calculateAddDeleteSet(getUserAuthoritiyNameSetByLogin(login),
          userVM.getAuthorities());
    addDelete.getDeleteSet().forEach(s -> userMapper.deleteUserAuthority(login, s));
    addDelete.getAddSet().forEach(s -> userMapper.insertUserAuthority(login, s));
  }

  private Set<String> getUserAuthoritiyNameSetByLogin(String login) {
    return userMapper.selectUserAuthorityNameSetByLogin(login);
  }

  /**
   * 更新用户状态
   *
   * @param id
   * @param status
   */
  public void updateStatus(long id, String status) {
    final User user = getUserById(id);
    user.setStatus(status);
    userMapper.updateById(user);

    // 前面user状态已改变，需要改变cache中的user，还需要获取user的authority信息
    addDefaultAuthority(user);
    cacheService.set(VMCACHENAME, user.getLogin(), userVMMapper.toVm(user));
  }

  /**
   * 重置密码
   *
   * @param passwordChangeVM
   */
  public void resetPassword(PasswordChangeVM passwordChangeVM) {
    String newPassword = passwordChangeVM.getNewPassword();
    if (StringUtils.isEmpty(newPassword)) {
      newPassword = Constants.INITIAL_PASSWORD;
    }

    final User user = getUserByLogin(passwordChangeVM.getLogin());
    setPassword(newPassword, user);
    userMapper.updateById(user);
  }

  /**
   * 获取包含权限信息的用户信息
   *
   * @param login
   * @return UserVM
   */
  //@Cacheable(value = VMCACHENAME, key = "#login")
  public UserVM getUserWithAuthoritiesByLogin(String login) {
    final User user = userMapper.getUserWithAuthoritiesByLogin(login);
    if (user == null) throw new ObjectNotFoundException("找不到用户");
    else {
      final UserVM userVM = userVMMapper.toVm(user);
      return userVM;
    }
  }

  /**
   * 获取当前登录用户信息
   *
   * @return UserVM
   */
  public UserVM getUserWithAuthorities() {
    final String userLogin = SecurityUtils.getCurrentUserLogin().get();
    return getUserWithAuthoritiesByLogin(userLogin);
  }

  /**
   * 获取给定id用户信息
   *
   * @param id
   * @return UserVM
   */
  public UserVM getUserWithAuthoritiesById(long id) {
    final User user = getUserById(id);
    final String userLogin = user.getLogin();
    return getUserWithAuthoritiesByLogin(userLogin);
  }

  public List<String> getAuthorities() {
    return authorityMapper.findAll().stream().map(Authority::getName).collect(Collectors.toList());
  }

  /**
   * 修改当前登录用户密码
   *
   * @param currentClearTextPassword。当前密码
   * @param newPassword                   新密码
   * @return 更新成功返回true
   */
  // password不会被cache，所以改变password不许变更cache
  public boolean updatePassword(String currentClearTextPassword, String newPassword) {
    final String userLogin = SecurityUtils.getCurrentUserLogin().get();
    return updatePassword(userLogin, currentClearTextPassword, newPassword);
  }

  /**
   * 修改指定用户密码
   * @param passwordChangeVM
   * @return
   */
  public boolean updatePassword(PasswordChangeVM passwordChangeVM) {
    return updatePassword(passwordChangeVM.getLogin(), passwordChangeVM.getCurrentPassword(), passwordChangeVM.getNewPassword());
  }

  public boolean updatePassword(String login, String currentClearTextPassword, String newPassword) {
    if (StringUtils.isEmpty(currentClearTextPassword)) throw new RuntimeException("原密码不能为空");
    if (StringUtils.isEmpty(newPassword)) throw new RuntimeException("新密码不能为空");
    final User user = getUserByLogin(login);
    if(user == null)
      throw new ObjectNotFoundException(String.format("login为%s的用户未找到", login));
    final String currentEncryptedPassword = user.getPassword();
    if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword))
      throw new RuntimeException("原密码错误");
    setPassword(newPassword, user);
    final int count = userMapper.updateById(user);
    if (count == 1) {
      log.debug("Changed password for User: {}", user);
    }
    return count == 1;
  }

  /**
   * 查找用户信息map
   *
   * @return
   */
  public Map<String, User> findUserMapByLoginList(List<String> loginList) {
    final List<User> userList = userMapper.selectList(new QueryWrapper<User>().in("login", loginList));
    final Map<String, User> map = new HashMap<>(userList.size());
    userList.forEach(user -> {
      map.put(user.getLogin(), user);
    });
    return map;
  }
}
