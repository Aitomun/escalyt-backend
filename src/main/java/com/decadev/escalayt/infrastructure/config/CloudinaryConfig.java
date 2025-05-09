package com.decadev.escalayt.infrastructure.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Value("${ESCALAYT_CLOUD_NAME}")
    private String cloudName;

    @Value("${ESCALAYT_CLOUD_KEY}")
    private String apiKey;

    @Value("${ESCALAYT_CLOUD_SECRET}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();

        config .put("cloud_name", cloudName);
        config.put("api_secret", apiSecret);
        config.put("api_key", apiKey);

        return new Cloudinary(config);
    }
}
