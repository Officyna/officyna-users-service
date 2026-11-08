package br.com.officyna.infrastructure.security;

import br.com.officyna.domain.user.entity.User;
import br.com.officyna.domain.user.entity.UserRole;
import br.com.officyna.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User buildEntity(String email, UserRole role, boolean active) {
        return User.builder()
                .id("1").name("João Silva").email(email)
                .password("encoded").userRole(role).active(active)
                .build();
    }

    @Test
    @DisplayName("Deve retornar UserDetails com email e role corretos")
    void loadUserByUsername_ShouldReturnUserDetails() {
        User entity = buildEntity("joao@email.com", UserRole.ADMIN, true);
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(entity));

        UserDetails details = userDetailsService.loadUserByUsername("joao@email.com");

        assertEquals("joao@email.com", details.getUsername());
        assertEquals("encoded", details.getPassword());
    }

    @Test
    @DisplayName("Deve mapear role para ROLE_{NOME} na authority")
    void loadUserByUsername_ShouldMapRoleCorrectly() {
        when(userRepository.findByEmail("admin@email.com"))
                .thenReturn(Optional.of(buildEntity("admin@email.com", UserRole.ADMIN, true)));

        UserDetails details = userDetailsService.loadUserByUsername("admin@email.com");

        assertTrue(details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals));
    }

    @Test
    @DisplayName("Deve mapear corretamente todas as roles disponíveis")
    void loadUserByUsername_ShouldMapAllRoles() {
        for (UserRole role : UserRole.values()) {
            User entity = buildEntity("user@email.com", role, true);
            when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(entity));

            UserDetails details = userDetailsService.loadUserByUsername("user@email.com");

            String expected = "ROLE_" + role.name();
            assertTrue(details.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .anyMatch(expected::equals),
                    "Role esperada: " + expected);
        }
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando email não existe")
    void loadUserByUsername_ShouldThrowException_WhenEmailNotFound() {
        when(userRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("naoexiste@email.com"));
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando usuário está inativo")
    void loadUserByUsername_ShouldThrowException_WhenUserInactive() {
        when(userRepository.findByEmail("inativo@email.com"))
                .thenReturn(Optional.of(buildEntity("inativo@email.com", UserRole.ATTENDANT, false)));

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("inativo@email.com"));
    }
}

