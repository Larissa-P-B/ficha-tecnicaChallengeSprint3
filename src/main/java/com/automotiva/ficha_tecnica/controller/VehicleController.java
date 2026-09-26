
package com.automotiva.ficha_tecnica.controller;

import com.automotiva.ficha_tecnica.infra.exception.BadRequestException;
import com.automotiva.ficha_tecnica.infra.exception.NotFoundException;
import com.automotiva.ficha_tecnica.service.VehicleService;
import com.automotiva.ficha_tecnica.service.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
@Tag(name = "Veículos", description = "Gerenciamento de veículos")
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    // ======================================================
    // 🔍 BUSCAR ESPECIFICAÇÕES
    // ======================================================

    @Operation(summary = "Buscar especificações do veículo")
    @PostMapping("/especificacoes")
    public ResponseEntity<VehicleResponse> buscarEspecificacoes(
            @Valid @RequestBody VehicleRequest request
    ) {

        VehicleResponse response =
                service.buscarEspecificacoes(request);

        return ResponseEntity.ok(response);
    }

    // ======================================================
    // ⚔️ COMPARAR VEÍCULOS
    // ======================================================

    @Operation(summary = "Comparar dois veículos")
    @PostMapping("/comparar")
    public ResponseEntity<ComparacaoResponse> comparar(
            @Valid @RequestBody ComparacaoRequest request
    ) {

        ComparacaoResponse response =
                service.comparar(
                        request.veiculo1(),
                        request.veiculo2()
                );

        return ResponseEntity.ok(response);
    }

    // ======================================================
    // ➕ CRIAR VEÍCULO
    // ======================================================

    @Operation(summary = "Criar novo veículo")
    @PostMapping
    public ResponseEntity<VehicleCrudResponse> criar(
            @Valid @RequestBody VehicleCreateRequest request,
            UriComponentsBuilder uriBuilder
    ) throws BadRequestException {

        VehicleCrudResponse response =
                service.criar(request);

        URI uri = uriBuilder
                .path("/api/veiculos/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    // ======================================================
    // 🔄 ATUALIZAR
    // ======================================================

    @Operation(summary = "Atualizar veículo")
    @PutMapping("/{id}")
    public ResponseEntity<VehicleCrudResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody VehicleCreateRequest request
    ) throws NotFoundException, BadRequestException {

        VehicleCrudResponse response =
                service.atualizar(id, request);

        return ResponseEntity.ok(response);
    }

    // ======================================================
    // 🔄 PATCH - ATUALIZAÇÃO PARCIAL
    // ======================================================

    @Operation(summary = "Atualizar parcialmente veículo")
    @PatchMapping("/{id}")
    public ResponseEntity<VehicleCrudResponse> atualizarParcialmente(
            @PathVariable Long id,
            @RequestBody VehicleUpdateRequest request
    ) throws NotFoundException {

        VehicleCrudResponse response =
                service.atualizarParcialmente(id, request);

        return ResponseEntity.ok(response);
    }

    // ======================================================
    // ❌ DELETE
    // ======================================================

    @Operation(summary = "Deletar veículo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id
    ) throws NotFoundException {

        service.deletar(id);

        return ResponseEntity.noContent().build();
    }

    // ======================================================
    // 📋 LISTAGEM PAGINADA
    // ======================================================

    @Operation(summary = "Buscar veículo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleCrudResponse> buscarPorId(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Listar veículos")
    @GetMapping
    public ResponseEntity<List<VehicleCrudResponse>> listar(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size

    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<VehicleCrudResponse> response =
                service.listarPaginado(pageable);

        return ResponseEntity.ok(response.getContent());
    }
}
