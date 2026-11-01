package br.com.officyna.domain.user.service;

import br.com.officyna.domain.user.entity.User;
import br.com.officyna.domain.user.exception.UserBusinessException;
import br.com.officyna.domain.user.exception.UserNotFoundException;
import br.com.officyna.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        log.info("Finding all active users");

        List<User> users = repository.findByActiveTrue();

        log.info("Active users found: {}", users.size());

        return users;
    }

    public User findById(String id) {
        log.info("Finding user by id: {}", id);

        User user = findEntityById(id);

        log.info("User found by id: {}", id);

        return user;
    }

    public User findByEmail(String email) {
        log.info("Finding user by email: {}", email);

        String normalizedEmail = normalizeEmail(email);

        User user = repository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Usuário não encontrado com este email: " + email
                        )
                );

        log.info("User found by email: {}", normalizedEmail);

        return user;
    }

    public User create(User user) {
        log.info("Creating user with email: {}", user.getEmail());

        validateAdminOrManager();

        String normalizedEmail = normalizeEmail(user.getEmail());
        Optional<User> userExist = repository.findByEmail(normalizedEmail);

        if (userExist.isPresent() && Boolean.TRUE.equals(userExist.get().getActive())) {
            log.warn("User creation failed. Email already registered: {}", normalizedEmail);

            throw new UserBusinessException(
                    "Já existe um usuário com este email: " + normalizedEmail
            );
        }

        user.setId(userExist.map(User::getId).orElse(null));
        user.setEmail(normalizedEmail);
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = repository.save(user);

        log.info("User created successfully with id: {}", savedUser.getId());

        return savedUser;
    }

    private void validateAdminOrManager() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean hasPermission = auth != null
                && auth.getAuthorities() != null
                && auth.getAuthorities().stream()
                .anyMatch(a ->
                        Objects.equals(a.getAuthority(), "ROLE_ADMIN")
                                || Objects.equals(a.getAuthority(), "ROLE_MANAGER")
                );

        if (!hasPermission) {
            log.warn("User creation denied. User does not have ADMIN or MANAGER permission");

            throw new UserBusinessException(
                    "Apenas ADMIN ou MANAGER podem criar usuários internos."
            );
        }

        log.debug("User creation permission validated");
    }

    private static @NonNull String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.toLowerCase(Locale.ROOT).trim();
    }

    public User update(String id, User changes) {
        log.info("Updating user with id: {}", id);

        User entity = findEntityById(id);

        String normalizedEmail = normalizeEmail(changes.getEmail());
        boolean emailChanged = !normalizeEmail(entity.getEmail()).equals(normalizedEmail);

        if (emailChanged && repository.existsByEmail(normalizedEmail)) {
            log.warn(
                    "User update failed. Email already registered: {}",
                    normalizedEmail
            );

            throw new UserBusinessException(
                    "Já existe um usuário com este email: " + normalizedEmail
            );
        }

        entity.setName(changes.getName());
        entity.setEmail(normalizedEmail);
        entity.setUserRole(changes.getUserRole());

        if (changes.getPassword() != null && !changes.getPassword().isBlank()) {
            entity.setPassword(passwordEncoder.encode(changes.getPassword()));
        }

        User updatedUser = repository.save(entity);

        log.info("User updated successfully with id: {}", id);

        return updatedUser;
    }

    public void delete(String id) {
        log.info("Deleting user with id: {}", id);

        User entity = findEntityById(id);

        entity.setActive(false);
        repository.save(entity);

        log.info("User deactivated successfully with id: {}", id);
    }

    public User findEntityById(String id) {
        log.debug("Searching user entity by id: {}", id);

        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return UserNotFoundException.of(id);
                });
    }
}

