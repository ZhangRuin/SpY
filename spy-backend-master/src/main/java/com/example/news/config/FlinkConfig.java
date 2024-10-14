package com.example.news.config;

import com.example.news.component.job.SaveJob;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlinkConfig {
    @Bean
    public SaveJob testJob(){
        return new SaveJob();
    }
}
