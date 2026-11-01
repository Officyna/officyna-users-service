package br.com.officyna.infrastructure.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "officyna-secret-key-change-in-production-min-256bits");
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);
    }

    @Test
    @DisplayName("Deve gerar token válido e extrair username e claims")
    void generateAndValidateToken() {
        UserDetails userDetails = new User(
                "admin@officyna.com",
                "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("admin@officyna.com", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));

        Claims claims = jwtService.extractAllClaims(token);
        assertEquals("ADMIN", claims.get("roles", String.class));
    }

    @Test
    @DisplayName("Deve gerar token com dados do usuário (id, name, email) nas claims a partir da entidade User")
    void generateToken_ShouldIncludeUserClaims() {
        UserDetails userDetails = new User(
                "joao.silva5@macanico.com",
                "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_MECHANIC"))
        );
        br.com.officyna.domain.user.entity.User user = br.com.officyna.domain.user.entity.User.builder()
                .id("6ae7742a7ba336cf95f18102")
                .name("João da Silva")
                .email("joao.silva5@macanico.com")
                .userRole(br.com.officyna.domain.user.entity.UserRole.MECHANIC)
                .build();

        String token = jwtService.generateToken(userDetails, user);

        assertNotNull(token);
        assertEquals("joao.silva5@macanico.com", jwtService.extractUsername(token));
        assertEquals("6ae7742a7ba336cf95f18102", jwtService.extractId(token));
        assertEquals("João da Silva", jwtService.extractName(token));
        assertEquals("joao.silva5@macanico.com", jwtService.extractEmail(token));

        Claims claims = jwtService.extractAllClaims(token);
        assertEquals("6ae7742a7ba336cf95f18102", claims.get("id", String.class));
        assertEquals("João da Silva", claims.get("name", String.class));
        assertEquals("joao.silva5@macanico.com", claims.get("email", String.class));
        assertEquals("MECHANIC", claims.get("roles", String.class));
    }

    @Test
    @DisplayName("Deve gerar token diretamente a partir da entidade User")
    void generateToken_DirectlyFromUserEntity() {
        br.com.officyna.domain.user.entity.User user = br.com.officyna.domain.user.entity.User.builder()
                .id("6ae7742a7ba336cf95f18102")
                .name("João da Silva")
                .email("joao.silva5@macanico.com")
                .userRole(br.com.officyna.domain.user.entity.UserRole.MECHANIC)
                .build();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertEquals("joao.silva5@macanico.com", jwtService.extractUsername(token));
        assertEquals("6ae7742a7ba336cf95f18102", jwtService.extractId(token));
        assertEquals("João da Silva", jwtService.extractName(token));
        assertEquals("joao.silva5@macanico.com", jwtService.extractEmail(token));
    }

    @Test
    @DisplayName("Deve retornar falso quando token pertence a outro usuário")
    void isTokenValid_ShouldReturnFalse_WhenUsernameDiffers() {
        UserDetails user1 = new User("user1@officyna.com", "pwd", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        UserDetails user2 = new User("user2@officyna.com", "pwd", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        String token = jwtService.generateToken(user1);

        assertFalse(jwtService.isTokenValid(token, user2));
    }
}

