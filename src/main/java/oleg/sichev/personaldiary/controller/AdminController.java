package oleg.sichev.personaldiary.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import oleg.sichev.personaldiary.dto.UpdateUserDTO;
import oleg.sichev.personaldiary.entity.User;
import oleg.sichev.personaldiary.service.AdminService;
import oleg.sichev.personaldiary.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        // TODO Вернуть список пользователей. Сделать с пагинацией
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable @Min(1) long id, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        // Изменения данных пользователя, все проверки, сохранение в репозитории происходят в adminService
        return ResponseEntity.ok(adminService.updateUser(id, updateUserDTO));
    }
}
