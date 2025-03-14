package com.example.getforexrate2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "spring.resttemplate")
@Data
public class AppConfig {

    private int connectionTimeout;
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(connectionTimeout))  // 設定連接超時
                .setReadTimeout(Duration.ofMillis(readTimeout))      // 設定讀取超時
                .build();
    }
}
