package com.example.hallucinationdetector.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http.authorizeHttpRequests(access -> access
                .requestMatchers(
                        "/dbadmin", "/dbadmin/**", "/Html/dbadmin*"
                ).hasRole("DB_ADMIN")
                .requestMatchers(
                        "/login", "/register",
                        "/Css/**", "/JavaScript/**", "/Images/**"
                ).permitAll()
                .anyRequest().authenticated()
        );

        http.formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> {
                        boolean databaseAdmin = authentication.getAuthorities().stream()
                                .anyMatch(authority ->
                                        authority.getAuthority().equals("ROLE_DB_ADMIN"));

                        if (databaseAdmin) {
                                response.sendRedirect(request.getContextPath() + "/dbadmin");
                        } else {
                                response.sendRedirect(request.getContextPath() + "/");
                        }
})
                .failureUrl("/login?error")
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
        );

        return http.build();
    }
}