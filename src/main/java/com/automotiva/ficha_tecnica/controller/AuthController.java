package com.automotiva.ficha_tecnica.controller;

import com.automotiva.ficha_tecnica.entity.usuario.Usuario;
import com.automotiva.ficha_tecnica.infra.exception.CredenciaisInvalidasException;
import com.automotiva.ficha_tecnica.repository.UsuarioRepository;
import com.automotiva.ficha_tecnica.service.TokenService;
import com.automotiva.ficha_tecnica.service.dto.LoginRequest;
import com.automotiva.ficha_tecnica.service.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        Usuario usuario = repository.findByLogin(request.login())
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!usuario.isAtivo()
                || !passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException();
        }

        return ResponseEntity.ok(tokenService.gerar(usuario));
    }
}