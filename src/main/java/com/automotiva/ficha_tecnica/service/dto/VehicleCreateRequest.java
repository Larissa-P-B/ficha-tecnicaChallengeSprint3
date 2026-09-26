package com.automotiva.ficha_tecnica.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public record VehicleCreateRequest(
        @NotBlank(message = "Marca é obrigatória")
        String marca,

        @NotBlank(message = "Modelo é obrigatório")
        String modelo,

        @NotBlank(message = "Versão é obrigatória")
        String versao,

        @NotEmpty(message = "Especificações não podem ser vazias")
        Map<
                @NotBlank(message = "Nome do atributo inválido") String,
                @NotBlank(message = "Valor do atributo inválido") String
                > especificacoes) {
}
