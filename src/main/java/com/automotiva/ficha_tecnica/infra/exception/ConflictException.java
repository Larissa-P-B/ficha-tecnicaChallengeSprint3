package com.automotiva.ficha_tecnica.infra.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String mensagem) {
        super(mensagem);
    }
}
