package com.example.news.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@ConfigurationProperties(prefix = "spring.rabbitmq")
@Data
public class RabbitmqConfig implements Serializable {
    private String host;
    private int port;
    private String username;
    private String password;
    private String virtualHost;
}
