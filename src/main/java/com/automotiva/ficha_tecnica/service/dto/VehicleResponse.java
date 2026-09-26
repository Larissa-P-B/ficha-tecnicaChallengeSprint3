package com.automotiva.ficha_tecnica.service.dto;



import java.util.Map;

public record VehicleResponse(
        String marca,
        String modelo,
        String versao,
        Map<String, String> especificacoes
) {}