package com.example.news.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.datasource.druid")
@Data
public class MysqlConfig {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
}
