package com.automotiva.ficha_tecnica.service;

import com.automotiva.ficha_tecnica.entity.usuario.Roles;
import com.automotiva.ficha_tecnica.entity.usuario.Usuario;
import com.automotiva.ficha_tecnica.infra.exception.BadRequestException;
import com.automotiva.ficha_tecnica.infra.exception.ConflictException;
import com.automotiva.ficha_tecnica.infra.exception.NotFoundException;
import com.automotiva.ficha_tecnica.repository.UsuarioRepository;
import com.automotiva.ficha_tecnica.service.dto.UsuarioRequest;
import com.automotiva.ficha_tecnica.service.dto.UsuarioResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void criarAdminInicial(String login, String senha) {
        var existente = repository.findByLogin(login);

        if (existente.isPresent()) {
            if (existente.get().getRoles() != Roles.ADMIN) {
                throw new IllegalStateException(
                        "O login inicial já pertence a um usuário que não é ADMIN"
                );
            }
            return; // Não recria o administrador a cada inicialização.
        }

        Usuario admin = new Usuario();
        admin.setLogin(login);
        admin.setSenha(passwordEncoder.encode(senha));
        admin.setRoles(Roles.ADMIN);
        admin.setAtivo(true);

        repository.save(admin);
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        String login = request.login().trim();
        if (repository.existsByLogin(login)) {
            throw new ConflictException("Login já cadastrado");
        }
        Usuario usuario = new Usuario();
        preencher(usuario, request, login);
        return UsuarioResponse.de(repository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(UsuarioResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscar(Long id) {
        return UsuarioResponse.de(encontrar(id));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request, String administrador) {
        Usuario usuario = encontrar(id);
        String login = request.login().trim();
        if (!usuario.getLogin().equals(login) && repository.existsByLogin(login)) {
            throw new ConflictException("Login já cadastrado");
        }
        if (usuario.getLogin().equals(administrador)
                && (!request.ativo() || request.roles() != Roles.ADMIN
                    || !usuario.getLogin().equals(login))) {
            throw new BadRequestException("Não é permitido remover o próprio acesso ADMIN");
        }
        preencher(usuario, request, login);
        return UsuarioResponse.de(repository.save(usuario));
    }

    @Transactional
    public void excluir(Long id, String administrador) {
        Usuario usuario = encontrar(id);
        if (usuario.getLogin().equals(administrador)) {
            throw new BadRequestException("Não é permitido excluir a própria conta");
        }
        repository.delete(usuario);
    }

    private Usuario encontrar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    private void preencher(Usuario usuario, UsuarioRequest request, String login) {
        usuario.setLogin(login);
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setRoles(request.roles());
        usuario.setAtivo(request.ativo());
    }
}
