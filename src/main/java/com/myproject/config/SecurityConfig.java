package com.myproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class SecurityConfig {
    private JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //h(cd)^2
        http.csrf().disable().cors().disable();//crsf: cross-site request forgery
        http.addFilterBefore(jwtFilter, AuthorizationFilter.class);
        //haap
        //http.authorizeHttpRequests().anyRequest().permitAll();
        http.authorizeHttpRequests()
                .requestMatchers("/api/users/register", "/api/users/login", "/api/users/verify")
                .permitAll()
                .requestMatchers("/api/users/update")
                .hasRole("USER")
                .anyRequest().authenticated();
        return http.build();
    }
}