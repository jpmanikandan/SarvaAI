package com.sarva.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.sarva.security.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/index.html", "/login.html", "/css/**", "/js/**",
                                                                "/api/voice/speak", "/api/chat/**", "/api/history/**",
                                                                "/h2-console/**", "/error")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/login.html")
                                                .successHandler(customAuthenticationSuccessHandler))
                                .formLogin(form -> form
                                                .loginPage("/login.html")
                                                .permitAll());

                return http.build();
        }
}
