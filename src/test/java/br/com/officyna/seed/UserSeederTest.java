package br.com.officyna.seed;

import br.com.officyna.infrastructure.persistence.mongodb.model.UserDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.UserMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSeederTest {

    @Mock
    private UserMongoRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserSeeder userSeeder;

    @Test
    @DisplayName("Deve criar usuário admin quando não existir")
    void seed_ShouldCreateAdmin_WhenNotExists() {
        when(repository.existsByEmail("admin@officyna.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded");

        userSeeder.seed();

        verify(repository).save(any(UserDocument.class));
    }

    @Test
    @DisplayName("Não deve criar usuário admin quando já existir")
    void seed_ShouldSkip_WhenAdminAlreadyExists() {
        when(repository.existsByEmail("admin@officyna.com")).thenReturn(true);

        userSeeder.seed();

        verify(repository, never()).save(any());
    }
}

