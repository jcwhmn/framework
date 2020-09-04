package com.yangzhou.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEFAULT_LANGUAGE = "en";
    
    public static final String INITIAL_PASSWORD = "123456";
    
    public static final String PAGE_SIZE = "10";
    public static final String CURRENT_PAGE = "0";
    
    public static final String layer_pre = "100";

		public static final String STATEFUL = "stateful";
    private Constants() {
    }
}
