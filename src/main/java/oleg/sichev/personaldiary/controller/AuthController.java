package oleg.sichev.personaldiary.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oleg.sichev.personaldiary.dto.AuthResponseDTO;
import oleg.sichev.personaldiary.dto.LoginRequestDTO;
import oleg.sichev.personaldiary.dto.RegisterRequestDTO;
import oleg.sichev.personaldiary.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO registerRequestDTO
            ) {
        return ResponseEntity.ok(authService.register(registerRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO
            ) {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }
}
