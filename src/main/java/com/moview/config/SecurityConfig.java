package com.moview.config;

import com.moview.common.oauth2.CustomSuccessHandler;
import com.moview.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final CustomSuccessHandler customSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {
        return http.authorizeHttpRequests((auth) -> auth
                        .anyRequest().permitAll())
                .cors((cors) -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)

                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(endpoint -> endpoint
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                        .redirectionEndpoint(redirectConfig -> redirectConfig
                                .baseUri("/api/login/**")))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();

    }
}
