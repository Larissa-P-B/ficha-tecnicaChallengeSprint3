package com.automotiva.ficha_tecnica.infra.security.config;

import com.automotiva.ficha_tecnica.repository.UsuarioRepository;
import com.automotiva.ficha_tecnica.infra.security.JwtAuthenticationFilter;
import com.automotiva.ficha_tecnica.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            TokenService tokenService,
            UsuarioRepository repository
    ) throws Exception {

        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((req, res, ex) ->
                                JwtAuthenticationFilter.escreverErro(
                                        res, 401, "Autenticação necessária"))
                        .accessDeniedHandler((req, res, ex) ->
                                JwtAuthenticationFilter.escreverErro(
                                        res, 403, "Acesso negado")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/login")
                        .permitAll()
                        .requestMatchers(
                                "/docs/**","/v3/api-docs.yaml","/swagger", "/swagger-ui.html",
                                "/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers("/api/usuarios", "/api/usuarios/**")
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/veiculos", "/api/veiculos/*")
                        .authenticated()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/veiculos/especificacoes",
                                "/api/veiculos/comparar")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/veiculos")
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT, "/api/veiculos/*")
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PATCH, "/api/veiculos/*")
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE, "/api/veiculos/*")
                        .hasRole("ADMIN")
                        .anyRequest().denyAll())
                .addFilterBefore(
                        new JwtAuthenticationFilter(tokenService, repository),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:}") String allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim).filter(origin -> !origin.isEmpty()).toList();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
