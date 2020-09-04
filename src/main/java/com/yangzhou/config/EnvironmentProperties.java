package com.yangzhou.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ConfigurationProperties(prefix = "istest",ignoreUnknownFields = false)
@Component
public class EnvironmentProperties {
    private Boolean test;
}
