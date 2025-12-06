package oleg.sichev.personaldiary.security;

import lombok.RequiredArgsConstructor;
import oleg.sichev.personaldiary.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // ИЗМЕНИТЕ: инжектируйте JwtService и UserDetailsService вместо фильтра
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Отключаем CSRF для REST API
                .csrf(AbstractHttpConfigurer::disable)

                // Настраиваем авторизацию запросов
                .authorizeHttpRequests(auth -> auth
                        // Публичные endpoint (доступны без авторизации)
                        .requestMatchers("/register", "/login").permitAll() // ИЗМЕНИТЕ: /auth/** на /register и /login
                        .requestMatchers("/public/**").permitAll()

                        // Документация, доступная для всех (если буду добавлять)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Health Check (для мониторинга - тоже доступно будет всем)
                        .requestMatchers("/actuator/health").permitAll()

                        // Защищенные endpoints
                        .requestMatchers("/diary/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Все остальное требует аутентификации
                        .anyRequest().authenticated()
                )

                // Отключаем сессии
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // СОЗДАВАЙТЕ фильтр через @Bean метод
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ДОБАВЬТЕ: метод для создания бина фильтра
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}