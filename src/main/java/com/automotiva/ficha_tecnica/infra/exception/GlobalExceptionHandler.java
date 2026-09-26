package com.automotiva.ficha_tecnica.infra.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;


    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<ErroResponse> handleNotFound(NotFoundException ex) {
            return erro(HttpStatus.NOT_FOUND, ex.getMessage());
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErroResponse> handleBadRequest(BadRequestException ex) {
            return erro(HttpStatus.BAD_REQUEST, ex.getMessage());
        }

        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ErroResponse> handleConflict(ConflictException ex) {
            return erro(HttpStatus.CONFLICT, ex.getMessage());
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErroResponse> handleDataConflict(DataIntegrityViolationException ex) {
            return erro(HttpStatus.CONFLICT, "Conflito de dados");
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErroResponse> handleValidation(MethodArgumentNotValidException ex) {
            String mensagem = ex.getBindingResult().getFieldErrors().stream()
                    .findFirst().map(e -> e.getDefaultMessage()).orElse("Dados inválidos");
            return erro(HttpStatus.BAD_REQUEST, mensagem);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErroResponse> handleGeneric(Exception ex) {
            return erro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor");
        }

        private ResponseEntity<ErroResponse> erro(HttpStatus status, String mensagem) {
            return ResponseEntity.status(status)
                    .body(new ErroResponse(status.value(), mensagem, LocalDateTime.now()));
        }

        @ExceptionHandler(CredenciaisInvalidasException.class)
        public ResponseEntity<ErroResponse> handleCredenciaisInvalidas(
                CredenciaisInvalidasException ex
        ) {
            return erro(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }
