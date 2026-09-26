package com.automotiva.ficha_tecnica.controller;

import com.automotiva.ficha_tecnica.service.UsuarioService;
import com.automotiva.ficha_tecnica.service.dto.UsuarioRequest;
import com.automotiva.ficha_tecnica.service.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento exclusivo de ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar usuário (ADMIN)")
    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(
            @Valid @RequestBody UsuarioRequest request, UriComponentsBuilder uriBuilder) {
        UsuarioResponse response = service.criar(request);
        URI uri = uriBuilder.path("/api/usuarios/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Listar usuários (ADMIN)")
    @GetMapping
    public List<UsuarioResponse> listar() {
        return service.listar();
    }

    @Operation(summary = "Buscar usuário (ADMIN)")
    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @Operation(summary = "Substituir usuário, inclusive senha (ADMIN)")
    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request, Authentication auth) {
        return service.atualizar(id, request, auth.getName());
    }

    @Operation(summary = "Excluir usuário (ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id, Authentication auth) {
        service.excluir(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
