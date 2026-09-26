package com.automotiva.ficha_tecnica.infra.exception;

public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() {
        super("Login ou senha inválidos");
    }
}