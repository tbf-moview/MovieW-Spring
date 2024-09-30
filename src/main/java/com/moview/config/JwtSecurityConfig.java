package com.moview.config;


import com.moview.common.JwtTokenProvider;
import com.moview.common.JWTFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtTokenProvider jwtTokenProvider;

    // Security Filter에 추가
    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(
                new JWTFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}
