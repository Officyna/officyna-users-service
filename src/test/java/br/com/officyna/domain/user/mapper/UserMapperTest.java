package br.com.officyna.domain.user.mapper;

import br.com.officyna.api.user.resources.UserRequest;
import br.com.officyna.api.user.resources.UserUpdateRequest;
import br.com.officyna.domain.user.entity.User;
import br.com.officyna.domain.user.entity.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    private UserRequest buildRequest() {
        return new UserRequest("João Silva", "joao@email.com", "senha123", UserRole.ATTENDANT);
    }

    private UserUpdateRequest buildUpdateRequest() {
        return new UserUpdateRequest("João Silva", "joao@email.com", null, UserRole.MANAGER);
    }

    @Test
    @DisplayName("toEntity(UserRequest) deve mapear todos os campos do request")
    void toEntity_ShouldMapAllFields() {
        User entity = mapper.toEntity(buildRequest());

        assertEquals("João Silva", entity.getName());
        assertEquals("joao@email.com", entity.getEmail());
        assertEquals("senha123", entity.getPassword());
        assertEquals(UserRole.ATTENDANT, entity.getUserRole());
        assertTrue(entity.getActive());
    }

    @Test
    @DisplayName("toEntity(UserUpdateRequest) deve mapear todos os campos do request de atualização")
    void toEntityUpdate_ShouldMapAllFields() {
        User entity = mapper.toEntity(buildUpdateRequest());

        assertEquals("João Silva", entity.getName());
        assertEquals("joao@email.com", entity.getEmail());
        assertNull(entity.getPassword());
        assertEquals(UserRole.MANAGER, entity.getUserRole());
        assertTrue(entity.getActive());
    }

    @Test
    @DisplayName("toEntity deve sempre setar active como true")
    void toEntity_ShouldSetActiveTrue() {
        assertTrue(mapper.toEntity(buildRequest()).getActive());
    }
}

