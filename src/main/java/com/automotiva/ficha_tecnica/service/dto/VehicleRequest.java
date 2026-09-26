package com.automotiva.ficha_tecnica.service.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record VehicleRequest(
        @NotBlank(message = "Marca é obrigatória")
        String marca,

        @NotBlank(message = "Modelo é obrigatório")
        String modelo,

        @NotBlank(message = "Versão é obrigatória")
        String versao,

        @NotEmpty(message = "Lista de atributos não pode ser vazia")
        List<@NotBlank(message = "Atributo não pode ser vazio") String> atributos
) {}
