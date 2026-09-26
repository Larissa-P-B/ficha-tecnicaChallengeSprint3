package com.automotiva.ficha_tecnica.service.dto;

import java.util.Map;

public record VehicleUpdateRequest(
        String marca,

        String modelo,

        String versao,

        Map<String, String> especificacoes
) {
}
