package com.automotiva.ficha_tecnica.service.dto;

import java.time.Instant;

public record LoginResponse(
        String token,
        String tipo,
        Instant expiraEm
) {}