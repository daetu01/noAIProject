package com.no.ai.api;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ApiConfig {

    private final Maple maple;
    private final Wow wow;

    public ApiConfig() {

        Dotenv dotenv = Dotenv.load();

        this.maple = new Maple(
                dotenv.get("MAPLE_API_KEY"),
                dotenv.get("MAPLE_BASE_URL")
        );

        this.wow = new Wow(
                dotenv.get("WOW_CLIENT_ID"),
                dotenv.get("WOW_CLIENT_SECRET"),
                dotenv.get("WOW_BASE_URL")
        );
    }

    @Getter
    @AllArgsConstructor
    public static class Maple {
        private String key;
        private String baseUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class Wow {
        private String clientId;
        private String clientSecret;
        private String baseUrl;
    }
}

