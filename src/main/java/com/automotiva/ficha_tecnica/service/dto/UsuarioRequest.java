package com.automotiva.ficha_tecnica.service.dto;

import com.automotiva.ficha_tecnica.entity.usuario.Roles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank @Size(max = 100) String login,
        @NotBlank @Size(min = 8) String senha,
        @NotNull Roles roles,
        @NotNull Boolean ativo
) {}
