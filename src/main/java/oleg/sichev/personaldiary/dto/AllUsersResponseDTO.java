package oleg.sichev.personaldiary.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AllUsersResponseDTO {
    private Long id;
    private String username;
    private String role;
    private String email;
    private String name;
    private String surname;
    private String middleName;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
