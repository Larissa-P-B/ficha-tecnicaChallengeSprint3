package com.automotiva.ficha_tecnica;

import com.automotiva.ficha_tecnica.controller.UsuarioController;
import com.automotiva.ficha_tecnica.infra.security.config.SecurityConfig;
import com.automotiva.ficha_tecnica.repository.UsuarioRepository;
import com.automotiva.ficha_tecnica.service.TokenService;
import com.automotiva.ficha_tecnica.service.UsuarioService;
import com.automotiva.ficha_tecnica.service.dto.UsuarioResponse;
import com.automotiva.ficha_tecnica.entity.usuario.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost:3000")
class UsuarioSecurityTest {
    @Autowired MockMvc mvc;
    @MockitoBean UsuarioService service;
    @MockitoBean TokenService tokenService;
    @MockitoBean UsuarioRepository repository;

    @Test
    void anonimoNaoCriaUsuario() throws Exception {
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"novo\",\"senha\":\"senha-forte\",\"roles\":\"USER\",\"ativo\":true}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userNaoCriaNemListaUsuarios() throws Exception {
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"novo\",\"senha\":\"senha-forte\",\"roles\":\"USER\",\"ativo\":true}"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/usuarios")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCriaSemExporSenha() throws Exception {
        when(service.criar(any())).thenReturn(new UsuarioResponse(9L, "novo", Roles.USER, true));
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"novo\",\"senha\":\"senha-forte\",\"roles\":\"USER\",\"ativo\":true}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/usuarios/9"))
                .andExpect(jsonPath("$.login").value("novo"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void dadosInvalidosRetornam400() throws Exception {
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"\",\"senha\":\"curta\",\"roles\":\"USER\",\"ativo\":true}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminExcluiUsuario() throws Exception {
        mvc.perform(delete("/api/usuarios/9")).andExpect(status().isNoContent());
        verify(service).excluir(9L, "admin");
    }

    @Test
    void preflightDaOrigemPermitidaRespondeSemToken() throws Exception {
        mvc.perform(options("/api/usuarios")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }
}
