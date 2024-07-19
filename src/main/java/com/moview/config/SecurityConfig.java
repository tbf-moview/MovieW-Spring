package com.moview.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

	@Autowired
	private CorsConfigurationSource corsConfigurationSource;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests((auth) -> auth
				.anyRequest().permitAll())
			.cors((cors) -> cors.configurationSource(corsConfigurationSource))
			.csrf(AbstractHttpConfigurer::disable)
			.build();

	}
}
