package oleg.sichev.personaldiary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserDTO {

    private String username;
    private String password;
    private String name;
    private String surname;
    private String middleName;
    private String phoneNumber;
    private String email;
    private String enabled;
    private String role;
}
