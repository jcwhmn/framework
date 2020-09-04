package com.yangzhou.security.provider;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.yangzhou.frame.domain.Authority;
import com.yangzhou.frame.domain.User;
import com.yangzhou.frame.mapper.AuthorityMapper;
import com.yangzhou.frame.service.UserService;
import com.yangzhou.security.tokens.MobileCodeAuthenticationToken;

@Component
public class MobileCodeAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthorityMapper authorityMapper;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String mobile = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
		String code = (String) authentication.getCredentials();
		if (StringUtils.isEmpty(code)) {
			throw new BadCredentialsException("验证码不能为空");
		}

		User user = userService.getUserByLogin(mobile);
		if (null == user) {
			throw new BadCredentialsException("用户不存在");
		}

		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(mobile, code,
				listUserGrantedAuthorities(user.getLogin()));
		result.setDetails(authentication.getDetails());
		return result;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		System.out.println(this.getClass().getName() + "---supports");
		return (MobileCodeAuthenticationToken.class.isAssignableFrom(authentication));
	}

	private Set<GrantedAuthority> listUserGrantedAuthorities(String login) {
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		if (StringUtils.isEmpty(login)) {
			return authorities;
		}
		Set<Authority> authority = authorityMapper.selectByUserLogin(login);
		for (Authority auth : authority) {
			authorities.add(new SimpleGrantedAuthority(auth.getName()));
		}
		return authorities;
	}

}
