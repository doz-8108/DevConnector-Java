package org.doz.config.security;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private static final List<AccessMappingPayload> accessMapping = new ArrayList<>();

    static {
        accessMapping.add(new AccessMappingPayload(HttpMethod.GET, "/api/profiles/**", true));
        accessMapping.add(new AccessMappingPayload(HttpMethod.POST, "/api/profiles/**", false));
        accessMapping.add(new AccessMappingPayload(HttpMethod.DELETE, "/api/profiles/**", false));

        accessMapping.add(new AccessMappingPayload(HttpMethod.POST, "/api/users/**", true));
        accessMapping.add(new AccessMappingPayload(HttpMethod.DELETE, "/api/users/**", false));

        accessMapping.add(new AccessMappingPayload(HttpMethod.POST, "/api/posts/**", false));
        accessMapping.add(new AccessMappingPayload(HttpMethod.GET, "/api/posts/**", true));
        accessMapping.add(new AccessMappingPayload(HttpMethod.DELETE, "/api/posts/**", false));

        accessMapping.add(new AccessMappingPayload(HttpMethod.GET, "/swagger-ui/**", true));
        accessMapping.add(new AccessMappingPayload(HttpMethod.GET, "/v3/api-docs/**", true));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> {
                    accessMapping.forEach(payload -> {
                        AuthorizeHttpRequestsConfigurer.AuthorizedUrl au = req.requestMatchers(
                                payload.getMethod(), payload.getUrl());
                        if (payload.getEnablePublicAccess()) {
                            au.permitAll();
                        } else {
                            au.authenticated();
                        }
                    });
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Setter
    @Getter
    @AllArgsConstructor
    private static class AccessMappingPayload {
        private HttpMethod method;
        private String url;
        private Boolean enablePublicAccess;
    }
}
