package com.echo.acknowledgehub.configuration;

import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.util.BaseURL;
import com.echo.acknowledgehub.util.JWTAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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
        return httpSecurity.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(request -> request
                // Configure routes to allow access for all users.
                .requestMatchers(BASE_URL + "/auth/**", BASE_URL + "/check", BASE_URL + "/test/**", BASE_URL + "/send-email").permitAll()
                // Configure routes to allow access for anonymous users.
                .requestMatchers(BASE_URL + "/auth/login").anonymous()
                // Configure routes to allow access for all authenticated users.
                .requestMatchers(BASE_URL + "/user/**",BASE_URL + "/notifications/**").authenticated()
                // Configure routes to allow access only for system admin.
                .requestMatchers(BASE_URL + "/ad/**").hasRole(EmployeeRole.ADMIN.name())
                // Configure routes to allow access only for the main HR section.
                .requestMatchers(BASE_URL + "/mr/**").hasRole(EmployeeRole.MAIN_HR.name())
                // Configure routes to allow access only for main HR assistance.
                .requestMatchers(BASE_URL + "/ma/**").hasRole(EmployeeRole.MAIN_HR_ASSISTANCE.name())
                //Configure routes to allow access only for the 'company/HR' subdirectory.
                .requestMatchers(BASE_URL + "/hr/**").hasRole(EmployeeRole.HR.name())
                //Configure routes to allow access only for the 'company/HR assistance' subdirectory.
                .requestMatchers(BASE_URL + "/ha/**").hasRole(EmployeeRole.HR_ASSISTANCE.name())
                // Configure routes to allow access only for stuff.
                .requestMatchers(BASE_URL + "/sf/**").hasRole(EmployeeRole.STAFF.name())
                // Configure routes to allow access for main HR & 'company/HR' subdirectory.
                .requestMatchers(BASE_URL + "/mhr/**").hasAnyRole(EmployeeRole.MAIN_HR.name(), EmployeeRole.HR.name())
                .requestMatchers(BASE_URL + "/announcement/create", BASE_URL + "/get-companies", BASE_URL + "/getCompanyById/**", BASE_URL + "/get-categories", BASE_URL + "/custom-target/**").hasAnyRole(EmployeeRole.MAIN_HR.name(), EmployeeRole.HR.name(), EmployeeRole.MAIN_HR_ASSISTANCE.name(), EmployeeRole.HR_ASSISTANCE.name())
        ).userDetailsService(USER_DETAILS_SERVICE).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).addFilterBefore(JWT_AUTHENTICATION_FILTER, UsernamePasswordAuthenticationFilter.class).build();
    }
}
