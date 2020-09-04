package com.yangzhou.security;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yangzhou.frame.domain.User;
import com.yangzhou.frame.mapper.AuthorityMapper;
import com.yangzhou.frame.mapper.UserMapper;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

  private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

  private final UserMapper      userMapper;
  private final AuthorityMapper authorityMapper;

  public DomainUserDetailsService(UserMapper userMapper, AuthorityMapper authorityMapper) {
    this.userMapper = userMapper;
    this.authorityMapper = authorityMapper;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(final String login) {
    log.debug("Authenticating {}", login);

    final String lowercaseLogin = login.toLowerCase(Locale.ENGLISH).trim();
    final User user = userMapper.selectOne(new QueryWrapper<User>().eq("login", login));
    if (user == null)
      throw new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database");
    else {
      user.setAuthorities(authorityMapper.selectByUserLogin(lowercaseLogin));
      return createSpringSecurityUser(lowercaseLogin, user);
    }
  }

  private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin,
      User user) {
    final List<GrantedAuthority> grantedAuthorities = user
        .getAuthorities().stream()
        .map(authority -> new SimpleGrantedAuthority(authority.getName()))
        .collect(Collectors.toList());
    return new org.springframework.security.core.userdetails.User(user.getLogin(),
        user.getPassword(),
        grantedAuthorities);
  }
}
