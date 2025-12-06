package oleg.sichev.personaldiary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private String token; // JWT токен
    private String type = "Bearer"; // Тип токена

    private Long userId;
    private String username;
    private String email;
    private String name;
    private String surname;
    private String role;

    // Можно добавить дополнительные поля
    private String phoneNumber;
    private String middleName;
}
