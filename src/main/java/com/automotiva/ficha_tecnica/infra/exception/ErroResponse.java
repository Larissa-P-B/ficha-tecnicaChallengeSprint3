package com.automotiva.ficha_tecnica.infra.exception;

import java.time.LocalDateTime;

public record ErroResponse(
        int status,
        String mensagem,
        LocalDateTime timestamp
) {
}
