package com.automotiva.ficha_tecnica.service.dto;



import java.util.Map;

public record ComparacaoResponse(
        Map<String, Map<String, String>> comparacao
) {}