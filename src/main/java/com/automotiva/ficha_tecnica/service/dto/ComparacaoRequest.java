package com.automotiva.ficha_tecnica.service.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ComparacaoRequest(
        @Valid
        @NotNull(message = "Veículo 1 é obrigatório")
        VehicleRequest veiculo1,

        @Valid
        @NotNull(message = "Veículo 2 é obrigatório")
        VehicleRequest veiculo2
) {}
