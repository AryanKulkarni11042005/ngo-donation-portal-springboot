package com.learning.store.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Matches the bcrypt hashes already stored by the Node backend.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/health").permitAll()
                        // Public: browsing campaigns, donating, certificate download/verify.
                        .requestMatchers(HttpMethod.GET, "/campaigns", "/campaigns/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/donations").permitAll()
                        .requestMatchers(HttpMethod.GET, "/certificates/**").permitAll()
                        // Admin-only writes.
                        .requestMatchers(HttpMethod.POST, "/campaigns").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/campaigns/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/donations/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/campaigns/*", "/donations/*").hasRole("ADMIN")
                        // Staff-only reads.
                        .requestMatchers(HttpMethod.GET, "/donations", "/donations/*").hasAnyRole("ADMIN", "VOLUNTEER")
                        .requestMatchers("/dashboard").hasAnyRole("ADMIN", "VOLUNTEER")
                        .requestMatchers("/auth/profile").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
