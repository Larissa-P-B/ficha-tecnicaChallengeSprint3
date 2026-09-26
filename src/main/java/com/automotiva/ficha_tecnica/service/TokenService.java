package com.automotiva.ficha_tecnica.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.automotiva.ficha_tecnica.entity.usuario.Usuario;
import com.automotiva.ficha_tecnica.service.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

@Service
public class TokenService {

    private final Algorithm algoritmo;
    private final JWTVerifier verifier;

    public TokenService(@Value("${api.security.token.secret}") String segredo) {

        if (segredo.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET deve ter ao menos 32 bytes");
        }
        this.algoritmo = Algorithm.HMAC256(segredo);
        this.verifier = JWT.require(algoritmo)
                .withIssuer("ficha-tecnica")
                .build();
    }

    public LoginResponse gerar(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expiracao = agora.plus(Duration.ofHours(2));

        String token = JWT.create()
                .withIssuer("ficha-tecnica")
                .withSubject(usuario.getLogin())
                .withClaim("roles", usuario.getRoles().name())
                .withIssuedAt(agora)
                .withExpiresAt(expiracao)
                .sign(algoritmo);

        return new LoginResponse(token, "Bearer", expiracao);
    }

    public DecodedJWT validar(String token) {
        DecodedJWT jwt = verifier.verify(token);

        if (jwt.getExpiresAtAsInstant() == null) {
            throw new IllegalArgumentException("Token sem expiração");
        }

        return jwt;
    }
}