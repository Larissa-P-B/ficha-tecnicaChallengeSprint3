package com.automotiva.ficha_tecnica.infra.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.automotiva.ficha_tecnica.entity.usuario.Usuario;
import com.automotiva.ficha_tecnica.repository.UsuarioRepository;
import com.automotiva.ficha_tecnica.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository repository;

    public JwtAuthenticationFilter(
            TokenService tokenService,
            UsuarioRepository repository
    ) {
        this.tokenService = tokenService;
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            try {
                var jwt = tokenService.validar(header.substring(7));
                String login = jwt.getSubject();

                if (login == null || login.isBlank()) {
                    escreverErro(response, 401, "Token inválido");
                    return;
                }

                Usuario usuario = repository.findByLogin(login).orElse(null);

                if (usuario == null || !usuario.isAtivo()
                        || usuario.getRoles() == null) {
                    escreverErro(response, 401, "Usuário inválido ou inativo");
                    return;
                }

                var permissao = new SimpleGrantedAuthority(
                        "ROLE_" + usuario.getRoles().name()
                );

                var autenticacao = new UsernamePasswordAuthenticationToken(
                        usuario.getLogin(), null, List.of(permissao)
                );

                SecurityContext contexto =
                        SecurityContextHolder.createEmptyContext();
                contexto.setAuthentication(autenticacao);
                SecurityContextHolder.setContext(contexto);

            } catch (JWTVerificationException | IllegalArgumentException ex) {
                SecurityContextHolder.clearContext();
                escreverErro(response, 401, "Token inválido ou expirado");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    public static void escreverErro(
            HttpServletResponse response,
            int status,
            String mensagem
    ) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"status\":" + status
                        + ",\"mensagem\":\"" + mensagem
                        + "\",\"timestamp\":\"" + Instant.now() + "\"}"
        );
    }
}