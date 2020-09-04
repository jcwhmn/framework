package com.yangzhou.frame.service;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yangzhou.config.UserVerifier;
import com.yangzhou.errors.LoginAlreadyUsedException;
import com.yangzhou.errors.PasswordVerificationException;
import com.yangzhou.frame.domain.Authority;
import com.yangzhou.frame.domain.User;
import com.yangzhou.frame.mapper.AuthorityMapper;
import com.yangzhou.frame.mapper.UserMapper;
import com.yangzhou.frame.vm.CreateUserVM;
import com.yangzhou.frame.vm.UserVM;
import com.yangzhou.frame.vmmapper.UserVMMapper;
import com.yangzhou.security.AuthoritiesConstants;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
  UserMapper userMapper = Mockito.mock(UserMapper.class);
  AuthorityMapper authorityMapper = Mockito.mock(AuthorityMapper.class);
  PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
  UserVMMapper    userVMMapper    = Mappers.getMapper(UserVMMapper.class);
  UserVerifier uerVerifier = Mockito.mock(UserVerifier.class);

  UserService userService;
  private final static String createUserLogin = "login2";

  @BeforeEach
  public void setup() {
    // userService = new UserService(userMapper, authorityMapper, passwordEncoder,
    // uerVerifier);
  }

  @Test
  public void testCreateUserSuccess() {
    final CreateUserVM userVM = createUserVM();
    final Authority authority = createAuthority();
    final String login = "login2";

    final User user = createUser();
    user.setPassword("encoded passwrod");
    when(userMapper.selectOne(ArgumentMatchers.argThat((QueryWrapper<User> uw) -> true))).thenReturn(null);
    when(userMapper.insert(ArgumentMatchers.argThat((User u) -> u.equals(user)))).thenReturn(1);
    when(authorityMapper.findOneByName(AuthoritiesConstants.USER)).thenReturn(authority);
    when(passwordEncoder.encode("password")).thenReturn("encoded passwrod");

    final UserVM newUserVM = userService.createUser(userVM);

    verify(passwordEncoder).encode("password");
    verify(authorityMapper).findOneByName(AuthoritiesConstants.USER);

    user.getAuthorities().add(authority);
    verify(userMapper).insert(user);

    verify(userMapper).selectOne(ArgumentMatchers.argThat((QueryWrapper<User> uw) -> true));

    userVM.getAuthorities().add(AuthoritiesConstants.USER);
    assertThat(userVM, Matchers.is(newUserVM));
  }

  @Test
  public void testCreateUserWithInvalidPasswordShouldFail() {
    when(userMapper.selectOne(ArgumentMatchers.argThat((QueryWrapper<User> uw) -> true))).thenReturn(createUser());
    try {
      final CreateUserVM userVM = createUserVM();
      userVM.setPassword("Less7");
      final UserVM newUserVM = userService.createUser(userVM);
    }catch (final PasswordVerificationException e) {
      assertThat(e.getTitle(), Matchers.is("无效的密码!"));
    }
  }

  @Test
  public void testCreateUserWithExistedLoginShouldFail1() {
    when(userMapper.selectOne(ArgumentMatchers.argThat((QueryWrapper<User> uw) -> true))).thenReturn(createUser());

    try {
      final UserVM newUserVM = userService.createUser(createUserVM());
    } catch (final LoginAlreadyUsedException e) {
      assertThat(e.getTitle(), Matchers.is("用户名已使用!"));
    }
  }

  private Authority createAuthority() {
    final Authority authority = new Authority();
    authority.setName(AuthoritiesConstants.USER);
    return authority;
  }

  private CreateUserVM createUserVM() {
    final CreateUserVM userVM = new CreateUserVM();
    userVM.setLogin(createUserLogin);
    userVM.setPassword("password");
    userVM.setName("name");
    userVM.setStatus("正常");
    return userVM;
  }

  private User createUser() {
    final User user = new User();
    user.setLogin(createUserLogin);
    user.setPassword("password");
    user.setName("name");
    user.setStatus("正常");
    return user;
  }
}
