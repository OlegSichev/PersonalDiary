package oleg.sichev.personaldiary.service;

import lombok.RequiredArgsConstructor;
import oleg.sichev.personaldiary.dto.AuthResponseDTO;
import oleg.sichev.personaldiary.dto.LoginRequestDTO;
import oleg.sichev.personaldiary.dto.RegisterRequestDTO;
import oleg.sichev.personaldiary.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        // Создаем пользователя из DTO
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setMiddleName(request.getMiddleName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole("ROLE_USER");

        // Сохраняем пользователя
        User savedUser = userService.registerUser(user);

        // Генерируем токен
        String jwtToken = jwtService.generateToken(savedUser);

        return AuthResponseDTO.builder()
                .token(jwtToken)
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .role(savedUser.getRole())
                .build();
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        // Аутентифицируем пользователя по USERNAME
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Получаем пользователя
        User user = (User) authentication.getPrincipal();

        // Генерируем token (в subject будет username)
        String jwtToken = jwtService.generateToken(user);

        return AuthResponseDTO.builder()
                .token(jwtToken)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}
