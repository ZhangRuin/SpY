package com.example.news.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Data
@Configuration
@ConfigurationProperties(prefix = "redis-constants")
public class RedisConstantsConfig {
    private String provinceCountKey = "provinceCount:";
    @DurationUnit(ChronoUnit.HOURS)
    private Duration provinceCountTTL;
    private String promptKey = "prompt:";
    @DurationUnit(ChronoUnit.HOURS)
    private Duration promptTTL;
}
