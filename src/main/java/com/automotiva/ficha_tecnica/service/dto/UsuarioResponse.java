package com.automotiva.ficha_tecnica.service.dto;

import com.automotiva.ficha_tecnica.entity.usuario.Roles;
import com.automotiva.ficha_tecnica.entity.usuario.Usuario;

public record UsuarioResponse(Long id, String login, Roles roles, boolean ativo) {
    public static UsuarioResponse de (Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getLogin(),
                usuario.getRoles(), usuario.isAtivo());
    }
}
