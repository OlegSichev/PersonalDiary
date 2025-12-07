package oleg.sichev.personaldiary.service;

import lombok.RequiredArgsConstructor;
import oleg.sichev.personaldiary.dto.UpdateUserDTO;
import oleg.sichev.personaldiary.entity.User;
import oleg.sichev.personaldiary.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Обновление данных пользователя АДМИНОМ. Можно менять даже роль или логин. Если пользователь сохранен,
    // возвращает true; Если пользователь пустой, возвращает false.
    public User updateUser(Long id, UpdateUserDTO updateUserDTO) {

        User user = userRepository.findById(id).orElseThrow(() -> {
            logger.error("Пользователь с данным ID не найден. ID: {}", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND);
        });

        if (updateUserDTO == null) {
            logger.info("UpdateUserDTO == null. НИКАКИХ ИЗМЕНЕНИЙ В БАЗУ ДАННЫХ НЕ ВНЕСЕНО.");
        } else {
            if (updateUserDTO.getUsername() != null && !user.getUsername().equals(updateUserDTO.getUsername())) {
                logger.info(messageForLoggerInUpdateUserMethod("Логин", true), user.getUsername(), updateUserDTO.getUsername());
                user.setUsername(updateUserDTO.getUsername());
            } else {
                logger.info(messageForLoggerInUpdateUserMethod("Логин", false), user.getUsername());
            }

            if (updateUserDTO.getName() != null && !user.getName().equals(updateUserDTO.getName())) {
                logger.info(messageForLoggerInUpdateUserMethod("Имя", true), user.getName(), updateUserDTO.getName());
                user.setName(updateUserDTO.getName());
            } else {
                logger.info(messageForLoggerInUpdateUserMethod("Имя", false), user.getName());
            }

            if (updateUserDTO.getSurname() != null && !user.getSurname().equals(updateUserDTO.getSurname())) {
                logger.info(messageForLoggerInUpdateUserMethod("Фамилия", true), user.getSurname(), updateUserDTO.getSurname());
                user.setSurname(updateUserDTO.getSurname());
            } else {
                logger.info(messageForLoggerInUpdateUserMethod("Фамилия", false), user.getSurname());
            }

            if (updateUserDTO.getMiddleName() != null && (!user.getMiddleName().equals(updateUserDTO.getMiddleName()))) {
                logger.info(messageForLoggerInUpdateUserMethod("Отчество", true), user.getMiddleName(), updateUserDTO.getMiddleName());
                user.setMiddleName(updateUserDTO.getMiddleName());
            } else {
                logger.info(messageForLoggerInUpdateUserMethod("Отчество", false), user.getMiddleName());
            }

            if (updateUserDTO.getPhoneNumber() != null && !user.getPhoneNumber().equals(updateUserDTO.getPhoneNumber())) {
                logger.info(messageForLoggerInUpdateUserMethod("Телефон", true), user.getPhoneNumber(), updateUserDTO.getPhoneNumber());
                user.setPhoneNumber(updateUserDTO.getPhoneNumber());
            } else {
                logger.info(messageForLoggerInUpdateUserMethod("Телефон", false), user.getPhoneNumber());
            }

            if (updateUserDTO.getEmail() != null && !user.getEmail().equals(updateUserDTO.getEmail())) {
                logger.info(messageForLoggerInUpdateUserMethod("Email", true), user.getEmail(), updateUserDTO.getEmail());
                user.setEmail(updateUserDTO.getEmail());
            } else {
                logger.info(messageForLoggerInUpdateUserMethod("Email", false), user.getEmail());
            }

            if (updateUserDTO.getRole() != null && !user.getRole().equals(updateUserDTO.getRole())) {
                logger.info(messageForLoggerInUpdateUserMethod("Роль", true), user.getRole(), updateUserDTO.getRole());
                user.setRole(updateUserDTO.getRole());
            } else {
                logger.info(messageForLoggerInUpdateUserMethod("Роль", false), user.getRole());
            }

            // Password сравнить не можем, поэтому его в конце меняем без if-else. В логе пишем зашифрованный вариант пароля.
            if (updateUserDTO.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
                logger.info("Пароль пользователя {} успешно изменен. Зашифрованный пароль выглядит так: {}", user.getUsername(), user.getPassword());
            }
        }

        return userRepository.save(user);
    }

    // ifElse = true - значит фраза: пользователя успешно изменено. Старое имя: {}. Новое имя: {}
    // ifElse = false - значит фраза: пользователя задали то же, что у него уже стоит. Изменений внесено не было. Имя пользователя: {}
    private String messageForLoggerInUpdateUserMethod(String fieldName, boolean ifElse) {
        if (ifElse) {
            return fieldName + " пользователя успешно изменено. Старое " + fieldName.toLowerCase() + " {}. Новое " + fieldName.toLowerCase() + " {}";
        } else {
            return fieldName + " пользователя задали то же, что у него уже стоит. Изменений внесено не было. " + fieldName.toLowerCase() + " пользователя: {}";
        }
    }
}
