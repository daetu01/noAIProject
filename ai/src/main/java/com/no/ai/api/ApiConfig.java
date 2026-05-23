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
    private final City city;

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

        this.city = new City(
                dotenv.get("SERVICE_KEY"),
                dotenv.get("value2")
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

    @Getter
    @AllArgsConstructor
    public static class City {
        private String serviceKey;
        private String value2;
    }
}

