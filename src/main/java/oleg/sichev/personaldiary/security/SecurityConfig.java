package oleg.sichev.personaldiary.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // TODO возможно, нужно будет скачать дополнительную зависимость в pom.xml
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        http
                // Отключаем CSRF для REST API
                .csrf(AbstractHttpConfigurer::disable)

                // Настраиваем авторизацию запросов
                .authorizeHttpRequests(auth -> auth
                // Публичные endpoint (доступны без авторизации)
                                .requestMatchers("/auth/**").permitAll() // Уточнить, насколько корректен адрес /api/ - ибо у меня это в адресе не планируется
                                .requestMatchers("/public/**").permitAll()

                        // Документация, доступная для всех (если буду добавлять)
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Health Check (для мониторинга - тоже доступно будет всем)
                                .requestMatchers("/actuator/health").permitAll()

                                // Защищенные endpoints (только с авторизация или только для определенных ролей)
                                .requestMatchers("/diary/**").authenticated()
                                .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Все остальное требует аутентификации
                                .anyRequest().authenticated()
                )

                // Отключаем сессии т.к. будем использовать JWT, а не сессионную авторизацию (stateless JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Добавляем JWT фильтр перед стандартным фильтром аутентификации
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); //TODO Выяснить, что такое jwtAuthFilter

        return http.build();
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
