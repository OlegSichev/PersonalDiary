package oleg.sichev.personaldiary.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import oleg.sichev.personaldiary.dto.UpdateUserDTO;
import oleg.sichev.personaldiary.service.AdminService;
import oleg.sichev.personaldiary.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@PageableDefault(size = 10, sort = "username", direction = Sort.Direction.ASC)
                                      Pageable pageable) {
        // Список возвращается с пагинацией
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PatchMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable @Min(1) long id, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        // Изменения данных пользователя, все проверки, сохранение в репозитории происходят в adminService
        return ResponseEntity.ok(adminService.updateUser(id, updateUserDTO));
    }
}
