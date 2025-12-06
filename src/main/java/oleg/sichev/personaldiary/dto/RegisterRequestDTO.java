package oleg.sichev.personaldiary.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "Логин обязателен")
    @Size(min = 3, max = 10)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Только буквы, цифры и _")
    private String username;

    @NotBlank(message = "Email обязателен")
    @Email
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6)
    private String password;

    private String name;
    private String surname;
    private String middleName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    private String phoneNumber;
}
