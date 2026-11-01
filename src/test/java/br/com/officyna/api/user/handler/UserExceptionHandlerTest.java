package br.com.officyna.api.user.handler;

import br.com.officyna.domain.user.exception.UserBusinessException;
import br.com.officyna.domain.user.exception.UserNotFoundException;
import br.com.officyna.infrastructure.exception.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserExceptionHandlerTest {

    private final UserExceptionHandler handler = new UserExceptionHandler();

    @Test
    @DisplayName("Deve tratar UserNotFoundException retornando 404")
    void handleNotFound_ShouldReturn404() {
        UserNotFoundException ex = UserNotFoundException.of("123");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("User not found with id: 123", response.getBody().message());
    }

    @Test
    @DisplayName("Deve tratar UserBusinessException retornando 400")
    void handleBusiness_ShouldReturn400() {
        UserBusinessException ex = new UserBusinessException("Email já cadastrado");

        ResponseEntity<ErrorResponse> response = handler.handleBusiness(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Email já cadastrado", response.getBody().message());
    }
}

