package br.com.officyna.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityHandlersTest {

    @Test
    @DisplayName("CustomAuthenticationEntryPoint deve retornar 401 com mensagem padrão")
    void commence_DefaultMessage_Returns401() throws Exception {
        CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.addHeader("X-Correlation-ID", "corr-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new BadCredentialsException("Bad credentials"));

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Authentication required"));
        assertTrue(response.getContentAsString().contains("corr-123"));
    }

    @Test
    @DisplayName("CustomAuthenticationEntryPoint deve retornar 401 com motivo customizado do atributo")
    void commence_CustomAttribute_Returns401() throws Exception {
        CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.setAttribute("auth_error_message", "Token expired");
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new BadCredentialsException("Bad credentials"));

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Unauthorized: Token expired"));
    }

    @Test
    @DisplayName("CustomAccessDeniedHandler deve retornar 403")
    void handle_AccessDenied_Returns403() throws Exception {
        CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/users");
        request.addHeader("correlationId", "corr-456");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("Denied"));

        assertEquals(403, response.getStatus());
        assertTrue(response.getContentAsString().contains("Access denied"));
        assertTrue(response.getContentAsString().contains("corr-456"));
    }
}

