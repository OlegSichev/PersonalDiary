package oleg.sichev.personaldiary.service;

import lombok.RequiredArgsConstructor;
import oleg.sichev.personaldiary.entity.User;
import oleg.sichev.personaldiary.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
// @RequiredArgsConstructor - создает конструктор для final полей и для полей помеченных @NonNull
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    // Регистрация нового пользователя
    public User registerUser(User user) {
        // Проверяем, что username уникальный
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }

        // Проверяем, что email уникальный
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        // Хешируем пароль перед сохранением
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // Найти пользователя по username (для логина)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username
                ));
    }

    // Найти пользователя по email (для проверки уникальности)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + email
                ));
    }

    // Найти пользователя по ID
    public User findById(Long id) {
            return userRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "User not found with ID: " + id
                    ));
    }

    // Обновить пользователя
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
