package com.bundlen.paymentservices.config;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.bundlen.paymentservices.constant.StringConstants.HEADER_AUTHORIZATION;
import static com.bundlen.paymentservices.constant.StringConstants.PROPERTY_CORS_ALLOWED_ORIGINS;
import static java.util.Arrays.asList;
import static java.util.Optional.*;
import static org.springframework.util.StringUtils.*;

@Data
@Configuration
public class CorsConfig {
    private static Logger logger = LogManager.getLogger(CorsConfig.class);
    @Autowired
    private Environment environment;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        String corsAllowedOrigins = ofNullable(environment.getRequiredProperty(PROPERTY_CORS_ALLOWED_ORIGINS))
                .filter(str -> !isEmpty(str)).orElse("*");
        configuration.setAllowedOrigins(Arrays.stream(corsAllowedOrigins.split(",")).collect(Collectors.toList()));
        configuration.setAllowedMethods(asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
        configuration.setAllowedHeaders(asList("X-Requested-With", "Origin", "Content-Type", "Accept", HEADER_AUTHORIZATION));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
