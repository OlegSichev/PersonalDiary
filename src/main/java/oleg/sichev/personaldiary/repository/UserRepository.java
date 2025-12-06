package oleg.sichev.personaldiary.repository;

import oleg.sichev.personaldiary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    // Проверить - существует ли пользователь с таким email (true or false)
    boolean existsByEmail(String email);

    // Проверить - существует ли пользователь с таким username (true or false)
    boolean existsByUsername(String username);
}
