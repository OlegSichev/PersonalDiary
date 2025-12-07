package oleg.sichev.personaldiary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Используется для входа в приложение
@Data
public class LoginRequestDTO {

    @NotBlank(message = "Логин обязателен")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    private String password;
}
