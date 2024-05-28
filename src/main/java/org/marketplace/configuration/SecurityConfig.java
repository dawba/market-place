package org.marketplace.configuration;

import org.marketplace.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public UserPassRequestFilter userPassRequestFilter(AuthenticationManager authenticationManager, CustomAuthenticationFailureHandler failureHandler) {
        UserPassRequestFilter filter = new UserPassRequestFilter("/api/user/login", authenticationManager, failureHandler);
        filter.setAuthenticationFailureHandler(failureHandler);
        return filter;
    }

    @Bean
    public CustomAuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager, CustomAuthenticationFailureHandler failureHandler) throws Exception {

        http.csrf().disable()
                .authorizeRequests()
                .requestMatchers(HttpMethod.GET, "/api/user/confirm-account").permitAll() // Permit GET requests to confirm-account
                .requestMatchers("/api/user/login", "/api/user/register").permitAll() // Permit all users to login and register
                .requestMatchers("/api/user/all", "/api/categories/add").hasRole(UserRole.ADMIN.getValue()) // Require ADMIN role for these endpoints
                .anyRequest().authenticated() // Require authentication for any other requests
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(userPassRequestFilter(authenticationManager, failureHandler), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("defaultUser")
//                .password(passwordEncoder().encode("defaultPass"))
//                .roles("USER");
//    }
}