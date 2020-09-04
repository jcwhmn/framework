package com.yangzhou.security.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import com.yangzhou.security.tokens.MobileCodeAuthenticationToken;



public class BhAuthenticationFilter extends AbstractAuthenticationProcessingFilter{

	public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
    public static final String SPRING_SECURITY_FORM_AUTYPE_KEY = "autype";

    private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
    private String passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;
    private String autypeParameter = SPRING_SECURITY_FORM_AUTYPE_KEY;
    private boolean postOnly = true;

    // ~ Constructors
    // ===================================================================================================

    public BhAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        String autype = obtainAutype(request);
        System.out.println(this.getClass().getName() + "----autype:" + autype);

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }
        
        if (autype == null) {
            autype = "";
        }

        username = username.trim();
        
        AbstractAuthenticationToken authRequest = null;
        switch(autype) {
        case "mobileCode":
            authRequest = new MobileCodeAuthenticationToken(username, password);
            break;
        default:
            authRequest = new UsernamePasswordAuthenticationToken(username, password);
            break;
        }

        /*UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                username, password);*/

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
    
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(passwordParameter);
    }
    
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(usernameParameter);
    }
    
    protected String obtainAutype(HttpServletRequest request) {
        return request.getParameter(autypeParameter);
    }
    
    protected void setDetails(HttpServletRequest request,
            AbstractAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
    
    public void setAutypeParameter(String autypeParameter) {
        Assert.hasText(autypeParameter, "Autype parameter must not be empty or null");
        this.autypeParameter = autypeParameter;
    }
    
    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }
    
    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }
    
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getUsernameParameter() {
        return usernameParameter;
    }

    public final String getPasswordParameter() {
        return passwordParameter;
    }


}
