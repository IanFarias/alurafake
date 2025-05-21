package br.com.alura.AluraFake.security;

import br.com.alura.AluraFake.infra.exception.AppException;
import br.com.alura.AluraFake.infra.security.TokenService;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.api.security.token.secret=my-secret-key"
})
public class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateTokenReturnsValidJwt() {
        User user = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR);

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"), "Token should start with JWT prefix");
    }

    @Test
    void testValidateTokenReturnsCorrectSubject() {
        User user = new User("paulo@alura.com.br", "123", Role.INSTRUCTOR);
        String token = tokenService.generateToken(user);

        String subject = tokenService.validateToken(token);

        assertEquals(user.getEmail(), subject);
    }

    @Test
    void testValidateTokenThrowsAppExceptionForInvalidToken() {
        String invalidToken = "dasd13";

        AppException exception = assertThrows(AppException.class, () -> {
            tokenService.validateToken(invalidToken);
        });

        assertEquals("The token was expected to have 3 parts, but got 0.", exception.getMessage());
        assertEquals(400, exception.getStatus().value());
    }

    @Test
    void testValidateTokenThrowsAppExceptionForBlankToken() {
        AppException exception = assertThrows(AppException.class, () -> {
            tokenService.validateToken("");
        });

        assertEquals(400, exception.getStatus().value());
    }
}
