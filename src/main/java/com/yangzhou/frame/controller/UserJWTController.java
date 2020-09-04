package com.yangzhou.frame.controller;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yangzhou.enums.StatusCode;
import com.yangzhou.frame.service.UserService;
import com.yangzhou.frame.vm.LoginVM;
import com.yangzhou.frame.vm.UserVM;
import com.yangzhou.security.jwt.JWTFilter;
import com.yangzhou.security.jwt.TokenProvider;
import com.yangzhou.util.MsgInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

/**
 * Controller to authenticate users.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Api(tags = "框架>登录管理")
public class UserJWTController {
	private final TokenProvider tokenProvider;
	private final AuthenticationManager authenticationManager;
	private final UserService userService;
	
	private final String loginStatus = "正常";

  /**
   * 登录系统。
   * 
   * @param loginVM
   * @return
   */
	@PostMapping("/authenticate")
	@ApiOperation("用户登录")
	public ResponseEntity<MsgInfo<UserVM>> authenticate(@Valid @RequestBody LoginVM loginVM) {
		final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginVM.getLogin(), loginVM.getPassword());

		final Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
    final boolean rememberMe = (loginVM.getRememberMe() == null) ? false : loginVM.getRememberMe();
		final String jwt = tokenProvider.createToken(authentication, rememberMe);
		
		final UserVM user = userService.getUserWithAuthoritiesByLogin(loginVM.getLogin());
		if(!loginStatus.equals(user.getStatus())) {
			throw new RuntimeException("您的账号已被冻结，暂时不能登录.");
		}
//		
//		//如果是经销商
//		if(UserTypeConstant.USER_JXS.equals(user.getUserType())) {
//			if(!userService.getDealerByCode(user.getLogin())) {
//				throw new RuntimeException("该经销商状态异常，暂时不能登录.");
//			}
//		}
//		
//		
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
		
		return new ResponseEntity<>(new MsgInfo(StatusCode.ok, user), httpHeaders, HttpStatus.OK);
	}

	/**
	 * Object to return as body in JWT Authentication.
	 */
	static class JWTToken {

		private String idToken;

		JWTToken(String idToken) {
			this.idToken = idToken;
		}

		@JsonProperty("id_token")
		String getIdToken() {
			return idToken;
		}

		void setIdToken(String idToken) {
			this.idToken = idToken;
		}
	}
}
