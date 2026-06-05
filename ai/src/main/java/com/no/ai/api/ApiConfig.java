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
    private final Weather weather;

    public ApiConfig() {

        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        this.maple = new Maple(
                getEnv(dotenv, "MAPLE_API_KEY"),
                getEnv(dotenv, "MAPLE_BASE_URL")
        );

        this.wow = new Wow(
                getEnv(dotenv, "WOW_CLIENT_ID"),
                getEnv(dotenv, "WOW_CLIENT_SECRET"),
                getEnv(dotenv, "WOW_BASE_URL")
        );

        this.city = new City(
                getEnv(dotenv, "SERVICE_KEY"),
                getEnv(dotenv, "SEOUL_API_BASE_URL")
        );

        this.weather = new Weather(
                getEnv(dotenv, "WEATHER_API_KEY"),
                getEnv(dotenv, "WEATHER_BASE_URL")
        );
    }

    private String getEnv(
            Dotenv dotenv,
            String key
    ) {
        String value =
                System.getenv(key);

        return value != null
                ? value
                : dotenv.get(key);
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

    @Getter
    @AllArgsConstructor
    public static class Weather {
        private String apikey;
        private String baseUrl;
    }
}

