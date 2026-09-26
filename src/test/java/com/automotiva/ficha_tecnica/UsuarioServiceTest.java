package com.automotiva.ficha_tecnica;

import com.automotiva.ficha_tecnica.entity.usuario.Roles;
import com.automotiva.ficha_tecnica.entity.usuario.Usuario;
import com.automotiva.ficha_tecnica.infra.exception.BadRequestException;
import com.automotiva.ficha_tecnica.infra.exception.ConflictException;
import com.automotiva.ficha_tecnica.repository.UsuarioRepository;
import com.automotiva.ficha_tecnica.service.UsuarioService;
import com.automotiva.ficha_tecnica.service.dto.UsuarioRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {
    private UsuarioRepository repository;
    private UsuarioService service;
    private PasswordEncoder encoder;

    @BeforeEach
    void configurar() {
        repository = mock(UsuarioRepository.class);
        encoder = new BCryptPasswordEncoder();
        service = new UsuarioService(repository, encoder);
    }

    @Test
    void criarGeraHashEEscondeSenhaNaResposta() {
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario usuario = inv.getArgument(0);
            usuario.setId(7L);
            assertNotEquals("senha-forte", usuario.getSenha());
            assertTrue(encoder.matches("senha-forte", usuario.getSenha()));
            return usuario;
        });
        var resposta = service.criar(new UsuarioRequest("novo", "senha-forte", Roles.USER, true));
        assertEquals(7L, resposta.id());
        assertEquals(Roles.USER, resposta.roles());
    }

    @Test
    void loginDuplicadoRetornaConflito() {
        when(repository.existsByLogin("novo")).thenReturn(true);
        assertThrows(ConflictException.class, () ->
                service.criar(new UsuarioRequest("novo", "senha-forte", Roles.USER, true)));
        verify(repository, never()).save(any());
    }

    @Test
    void adminNaoPodeExcluirPropriaConta() {
        Usuario admin = new Usuario();
        admin.setId(1L);
        admin.setLogin("admin");
        when(repository.findById(1L)).thenReturn(Optional.of(admin));
        assertThrows(BadRequestException.class, () -> service.excluir(1L, "admin"));
        verify(repository, never()).delete(any());
    }
}
