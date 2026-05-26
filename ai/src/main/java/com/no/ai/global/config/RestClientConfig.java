package com.no.ai.global.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient restClient() {

        // request Timeout 코드 10초 기준.
        JdkClientHttpRequestFactory factory =
                new JdkClientHttpRequestFactory();

        factory.setReadTimeout(Duration.ofSeconds(30));

        return RestClient.builder()
                .baseUrl("http://openapi.seoul.go.kr:8088")
                .requestFactory(factory)
                .build();
    }
}
