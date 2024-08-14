package com.echo.acknowledgehub.configuration;

import com.echo.acknowledgehub.persistence.constant.EmployeeRole;
import com.echo.acknowledgehub.util.BaseURL;
import com.echo.acknowledgehub.util.JWTAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.logging.Logger;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private static final Logger LOGGER = Logger.getLogger(SecurityConfig.class.getName());
    private final UserDetailsService USER_DETAILS_SERVICE;
    private final JWTAuthenticationFilter JWT_AUTHENTICATION_FILTER;
    private final BaseURL BASE_URL;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers(BASE_URL + "/test", BASE_URL + "/login").permitAll()
                                .requestMatchers(BASE_URL + "/ad/**").access((authentication, requestContext) ->
                                        new AuthorizationDecision(
                                                authentication.get().getAuthorities().stream()
                                                        .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(EmployeeRole.ADMIN.name()))
                                        )
                                ).requestMatchers(BASE_URL + "/mr/**").access((authentication, requestContext) ->
                                        new AuthorizationDecision(
                                                authentication.get().getAuthorities().stream()
                                                        .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(EmployeeRole.ADMIN.name()))
                                        )
                                ).requestMatchers(BASE_URL + "/ma/**").access((authentication, requestContext) ->
                                        new AuthorizationDecision(
                                                authentication.get().getAuthorities().stream()
                                                        .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(EmployeeRole.ADMIN.name()))
                                        )
                                ).requestMatchers(BASE_URL + "/hr/**").access((authentication, requestContext) ->
                                        new AuthorizationDecision(
                                                authentication.get().getAuthorities().stream()
                                                        .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(EmployeeRole.ADMIN.name()))
                                        )
                                ).requestMatchers(BASE_URL + "/ha/**").access((authentication, requestContext) ->
                                        new AuthorizationDecision(
                                                authentication.get().getAuthorities().stream()
                                                        .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(EmployeeRole.ADMIN.name()))
                                        )
                                ).requestMatchers(BASE_URL + "/sf/**").access((authentication, requestContext) ->
                                        new AuthorizationDecision(
                                                authentication.get().getAuthorities().stream()
                                                        .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(EmployeeRole.ADMIN.name()))
                                        )
                                )
                                .anyRequest().authenticated()

                )
                .userDetailsService(USER_DETAILS_SERVICE)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(JWT_AUTHENTICATION_FILTER, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
