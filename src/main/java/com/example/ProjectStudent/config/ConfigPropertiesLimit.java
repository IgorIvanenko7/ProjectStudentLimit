package com.example.ProjectStudent.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring")
public class ConfigPropertiesLimit {

    private DbProperties datasource;

    @Data
    @RequiredArgsConstructor
    public static class DbProperties {
            private String url;
            private String username;
            private String password;
            private int size_pool;
    }
}
