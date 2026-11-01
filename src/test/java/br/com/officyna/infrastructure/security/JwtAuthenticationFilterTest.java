package br.com.officyna.infrastructure.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve prosseguir sem autenticar quando não há header Authorization")
    void doFilterInternal_NoHeader_ContinuesChain() throws Exception {
        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve autenticar usuário quando token Bearer é válido")
    void doFilterInternal_ValidToken_SetsAuthentication() throws Exception {
        request.addHeader("Authorization", "Bearer valid-token");
        UserDetails userDetails = new User("admin@officyna.com", "pwd", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(jwtService.extractUsername("valid-token")).thenReturn("admin@officyna.com");
        when(userDetailsService.loadUserByUsername("admin@officyna.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid-token", userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("admin@officyna.com", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve registrar atributo de erro quando usuário não é encontrado")
    void doFilterInternal_UserNotFound_SetsErrorAttribute() throws Exception {
        request.addHeader("Authorization", "Bearer token");

        when(jwtService.extractUsername("token")).thenReturn("inexistente@officyna.com");
        when(userDetailsService.loadUserByUsername("inexistente@officyna.com"))
                .thenThrow(new UsernameNotFoundException("Não encontrado"));

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("Não encontrado", request.getAttribute("auth_error_message"));
        verify(filterChain).doFilter(request, response);
    }
}

