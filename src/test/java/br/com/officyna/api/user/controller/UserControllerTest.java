package br.com.officyna.api.user.controller;

import br.com.officyna.api.user.resources.UserRequest;
import br.com.officyna.api.user.resources.UserResponse;
import br.com.officyna.api.user.resources.UserUpdateRequest;
import br.com.officyna.domain.user.controller.UserControllerAdapter;
import br.com.officyna.domain.user.entity.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserControllerAdapter userControllerAdapter;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Deve retornar lista de todos os usuários com status 200")
    void findAll_ShouldReturnOk() throws Exception {
        when(userControllerAdapter.findAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(userControllerAdapter, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há usuários")
    void findAll_ShouldReturnEmptyList() throws Exception {
        when(userControllerAdapter.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userControllerAdapter, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar usuário por ID com status 200")
    void findById_ShouldReturnOk() throws Exception {
        String id = "usr-1";
        when(userControllerAdapter.findById(id)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk());

        verify(userControllerAdapter, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve retornar usuário por email com status 200")
    void findByEmail_ShouldReturnOk() throws Exception {
        String email = "joao@email.com";
        when(userControllerAdapter.findByEmail(email)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/users/email/{email}", email))
                .andExpect(status().isOk());

        verify(userControllerAdapter, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deve criar usuário e retornar status 201")
    void create_ShouldReturnCreated() throws Exception {
        when(userControllerAdapter.create(any(UserRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated());

        verify(userControllerAdapter, times(1)).create(any(UserRequest.class));
    }

    @Test
    @DisplayName("Deve atualizar usuário e retornar status 200")
    void update_ShouldReturnOk() throws Exception {
        String id = "usr-1";
        when(userControllerAdapter.update(eq(id), any(UserUpdateRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUpdateRequest())))
                .andExpect(status().isOk());

        verify(userControllerAdapter, times(1)).update(eq(id), any(UserUpdateRequest.class));
    }

    @Test
    @DisplayName("Deve desativar usuário e retornar status 204")
    void delete_ShouldReturnNoContent() throws Exception {
        String id = "usr-1";
        doNothing().when(userControllerAdapter).delete(id);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());

        verify(userControllerAdapter, times(1)).delete(id);
    }

    private UserRequest buildRequest() {
        return new UserRequest("João Silva", "joao@email.com", "Senha123", UserRole.ATTENDANT);
    }

    private UserUpdateRequest buildUpdateRequest() {
        return new UserUpdateRequest("João Silva", "joao@email.com", null, UserRole.ATTENDANT);
    }

    private UserResponse buildResponse() {
        return UserResponse.builder()
                .id("usr-1")
                .name("João Silva")
                .email("joao@email.com")
                .userRole(UserRole.ATTENDANT)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

