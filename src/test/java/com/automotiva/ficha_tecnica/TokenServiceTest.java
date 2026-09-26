package com.automotiva.ficha_tecnica;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.automotiva.ficha_tecnica.service.TokenService;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private static final String SEGREDO =
            "segredo-exclusivo-dos-testes-com-mais-de-32-caracteres";

    private final TokenService service = new TokenService(SEGREDO);

    @Test
    void aceitaTokenAssinadoEValido() {
        String token = criarToken(Instant.now().plusSeconds(60));

        assertEquals("admin", service.validar(token).getSubject());
    }

    @Test
    void rejeitaAssinaturaAlterada() {
        String token = criarToken(Instant.now().plusSeconds(60));

        assertThrows(
                JWTVerificationException.class,
                () -> service.validar(token + "alterado")
        );
    }

    @Test
    void rejeitaTokenExpirado() {
        String token = criarToken(Instant.now().minusSeconds(60));

        assertThrows(
                JWTVerificationException.class,
                () -> service.validar(token)
        );
    }

    private String criarToken(Instant expiracao) {
        return JWT.create()
                .withIssuer("ficha-tecnica")
                .withSubject("admin")
                .withExpiresAt(expiracao)
                .sign(Algorithm.HMAC256(SEGREDO));
    }
}