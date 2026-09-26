package com.automotiva.ficha_tecnica;

import com.automotiva.ficha_tecnica.infra.exception.NotFoundException;
import com.automotiva.ficha_tecnica.infra.security.config.SecurityConfig;
import com.automotiva.ficha_tecnica.controller.VehicleController;
import com.automotiva.ficha_tecnica.repository.UsuarioRepository;
import com.automotiva.ficha_tecnica.service.TokenService;
import com.automotiva.ficha_tecnica.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleController.class)
@Import(SecurityConfig.class)
class VehicleSecurityTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private VehicleService vehicleService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void consultaSemTokenRetorna401() throws Exception {
        mvc.perform(get("/api/veiculos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userPodeConsultar() throws Exception {
        when(vehicleService.listarPaginado(any(Pageable.class)))
                .thenReturn(Page.empty());

        mvc.perform(get("/api/veiculos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userNaoPodeCriar() throws Exception {
        mvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPodeExcluir() throws Exception, NotFoundException {
        mvc.perform(delete("/api/veiculos/999"))
                .andExpect(status().isNoContent());

        verify(vehicleService).deletar(999L);
    }
}