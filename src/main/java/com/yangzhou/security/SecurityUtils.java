package com.yangzhou.security;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.yangzhou.frame.domain.User;
import com.yangzhou.frame.mapper.UserMapper;
import com.yangzhou.util.SpringUtil;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {
    private static final String NO_LOGIN_USER = "No Login User";

    private SecurityUtils() {
    }

    public static String getRemoteAddress() {
      final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
      return request.getRemoteAddr();
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user
     */
    public static Optional<String> getCurrentUserLogin() {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
                .ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof UserDetails) {
                        final UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                        return springSecurityUser.getUsername();
                    } else if (authentication.getPrincipal() instanceof String)
                        return (String) authentication.getPrincipal();
                    return null;
                });
    }

    public static String getCurrentLoginName() {
        return getCurrentUserLogin().orElseGet(() -> NO_LOGIN_USER);
    }

  public static User getCurrentUser() {
    	final String login = getCurrentLoginName();
    	if(login.equals(NO_LOGIN_USER)) return null;
    	final UserMapper userMapper = (UserMapper) SpringUtil.getBean("userMapper");
    return userMapper.getUserWithAuthoritiesByLogin(login);
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user
     */
    public static Optional<String> getCurrentUserJWT() {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
                .ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }

    public static String getCurrentJWTString() {
        return getCurrentUserJWT().orElseGet(() -> null);
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
                .ofNullable(securityContext.getAuthentication())
                .map(authentication -> authentication
                        .getAuthorities().stream()
                        .noneMatch(grantedAuthority -> grantedAuthority
                                .getAuthority().equals(AuthoritiesConstants.ANONYMOUS)))
                .orElse(false);
    }

    public static boolean isRefreshed() {
        if (isAuthenticated()) {
            final SecurityContext securityContext = SecurityContextHolder.getContext();
            if (securityContext.getAuthentication() == null)
                return false;
            return (boolean) Optional
                    .ofNullable(securityContext.getAuthentication().getDetails()).orElseGet(() -> false);
        } else return false;
    }

    /**
     * If the current user has a specific authority (security role).
     * <p>
     * The name of this method comes from the isUserInRole() method in the Servlet API
     *
     * @param authority
     *            the authority to check
     * @return true if the current user has the authority, false otherwise
     */
    public static boolean isCurrentUserInRole(String authority) {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional
                .ofNullable(securityContext.getAuthentication())
                .map(authentication -> authentication
                        .getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority)))
                .orElse(false);
    }
}
