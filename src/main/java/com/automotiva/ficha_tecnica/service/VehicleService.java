

package com.automotiva.ficha_tecnica.service;

import com.automotiva.ficha_tecnica.entity.Atributo;
import com.automotiva.ficha_tecnica.entity.Especificacao;
import com.automotiva.ficha_tecnica.entity.Veiculo;
import com.automotiva.ficha_tecnica.infra.exception.BadRequestException;
import com.automotiva.ficha_tecnica.infra.exception.NotFoundException;
import com.automotiva.ficha_tecnica.repository.AtributoRepository;
import com.automotiva.ficha_tecnica.repository.EspecificacaoRepository;
import com.automotiva.ficha_tecnica.repository.VehicleRepository;
import com.automotiva.ficha_tecnica.service.dto.*;
import com.automotiva.ficha_tecnica.util.StringNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private static final Logger log = LoggerFactory.getLogger(VehicleService.class);

    private final EspecificacaoRepository especificacaoRepository;
    private final VehicleRepository vehicleRepository;
    private final AtributoRepository atributoRepository;

    public VehicleService(
            EspecificacaoRepository especificacaoRepository,
            VehicleRepository vehicleRepository,
            AtributoRepository atributoRepository
    ) {
        this.especificacaoRepository = especificacaoRepository;
        this.vehicleRepository = vehicleRepository;
        this.atributoRepository = atributoRepository;
    }

    // ======================================================
    // 🔍 BUSCAR ESPECIFICAÇÕES
    // ======================================================

    @Transactional(readOnly = true)
    public VehicleResponse buscarEspecificacoes(VehicleRequest request) {

        List<String> atributos = normalizarLista(request.atributos());

        List<Especificacao> specs = especificacaoRepository.buscarEspecificacoes(
                request.marca(),
                request.modelo(),
                request.versao(),
                atributos
        );

        Map<String, String> resultado = new LinkedHashMap<>();

        for (String atributo : atributos) {
            resultado.put(atributo, "Não disponível");
        }

        for (Especificacao e : specs) {

            String nome = normalizar(e.getAtributo().getNome());

            String valorFormatado;

            if ("X".equalsIgnoreCase(e.getValor())) {
                valorFormatado = "Disponível";
            } else if ("0".equals(e.getValor())) {
                valorFormatado = "Não disponível";
            } else {
                valorFormatado = e.getValor();
            }

            resultado.put(nome, valorFormatado);
        }

        return new VehicleResponse(
                request.marca(),
                request.modelo(),
                request.versao(),
                resultado
        );
    }

    // ======================================================
    // ⚔️ COMPARAÇÃO
    // ======================================================

    @Transactional(readOnly = true)
    public ComparacaoResponse comparar(VehicleRequest v1, VehicleRequest v2) {

        List<String> atributos = normalizarLista(v1.atributos());

        VehicleResponse r1 = buscarEspecificacoes(v1);
        VehicleResponse r2 = buscarEspecificacoes(v2);

        Map<String, Map<String, String>> resultado = new LinkedHashMap<>();

        String chaveV1 = montarNomeVeiculo(v1);
        String chaveV2 = montarNomeVeiculo(v2);

        for (String atributo : atributos) {

            Map<String, String> linha = new LinkedHashMap<>();

            linha.put(
                    chaveV1,
                    Optional.ofNullable(r1.especificacoes().get(atributo))
                            .orElse("Não disponível")
            );

            linha.put(
                    chaveV2,
                    Optional.ofNullable(r2.especificacoes().get(atributo))
                            .orElse("Não disponível")
            );

            resultado.put(atributo, linha);
        }

        return new ComparacaoResponse(resultado);
    }

    // ======================================================
    // ➕ CREATE
    // ======================================================

    @Transactional
    public VehicleCrudResponse criar(VehicleCreateRequest request) throws BadRequestException {

        validarDuplicidade(request);

        Veiculo vehicle = Veiculo.builder()
                .marca(request.marca())
                .modelo(request.modelo())
                .versao(request.versao())
                .build();

        vehicle = vehicleRepository.save(vehicle);

        salvarEspecificacoes(vehicle, request.especificacoes());

        log.info(
                "Veículo criado: id={}, marca={}, modelo={}",
                vehicle.getId(),
                vehicle.getMarca(),
                vehicle.getModelo()
        );

        return montarResponse(vehicle, request.especificacoes());
    }

    // ======================================================
    // 🔄 UPDATE
    // ======================================================

    @Transactional
    public VehicleCrudResponse atualizar(Long id, VehicleCreateRequest request) throws NotFoundException, BadRequestException {

        Veiculo vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado: " + id));

        vehicle.setMarca(request.marca());
        vehicle.setModelo(request.modelo());
        vehicle.setVersao(request.versao());

        vehicleRepository.save(vehicle);

        List<Especificacao> existentes =
                especificacaoRepository.findByVehicleId(id);

        Map<String, Especificacao> mapaExistentes = existentes.stream()
                .collect(Collectors.toMap(
                        e -> normalizar(e.getAtributo().getNome()),
                        e -> e
                ));

        Map<String, String> especificacoes =
                Optional.ofNullable(request.especificacoes())
                        .orElse(Collections.emptyMap());

        List<Especificacao> paraSalvar = new ArrayList<>();

        for (Map.Entry<String, String> entry : especificacoes.entrySet()) {

            String nome = normalizar(entry.getKey());
            String valor = entry.getValue();

            Especificacao existente = mapaExistentes.get(nome);

            if (existente != null) {

                existente.setValor(valor);

                paraSalvar.add(existente);

            } else {

                Atributo atributo = atributoRepository
                        .findByNomeIgnoreCase(nome)
                        .orElseThrow(() -> new BadRequestException("Atributo não encontrado: " + nome));

                Especificacao nova = Especificacao.builder()
                        .vehicle(vehicle)
                        .atributo(atributo)
                        .valor(valor)
                        .build();

                paraSalvar.add(nova);
            }
        }

        especificacaoRepository.saveAll(paraSalvar);

        log.info("Veículo atualizado: id={}", id);

        return montarResponse(vehicle, especificacoes);
    }
    // ======================================================
    // 🔄 PATCH - ATUALIZAÇÃO PARCIAL
    // ======================================================

    @Transactional
    public VehicleCrudResponse atualizarParcialmente(
            Long id,
            VehicleUpdateRequest request
    ) throws NotFoundException {

        // ==============================================
        // Buscar veículo
        // ==============================================

        Veiculo vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado: " + id));

        // ==============================================
        // Atualizar somente campos enviados
        // ==============================================

        if (request.marca() != null) {
            vehicle.setMarca(request.marca());
        }

        if (request.modelo() != null) {
            vehicle.setModelo(request.modelo());
        }

        if (request.versao() != null) {
            vehicle.setVersao(request.versao());
        }

        // ==============================================
        // Atualizar especificações
        // ==============================================

        if (request.especificacoes() != null) {

            atualizarEspecificacoes(
                    vehicle,
                    request.especificacoes()
            );
        }

        // ==============================================
        // Salvar veículo
        // ==============================================

        vehicleRepository.save(vehicle);

        // ==============================================
        // Retornar response completo
        // ==============================================

        return montarResponse(vehicle);
    }

    // ======================================================
    // 🔄 ATUALIZAR ESPECIFICAÇÕES
    // ======================================================

    private void atualizarEspecificacoes(
            Veiculo vehicle,
            Map<String, String> especificacoes
    ) {

        especificacoes.forEach((nomeAtributo, valor) -> {

            // ==========================================
            // Buscar atributo ou criar
            // ==========================================

            Atributo atributo = atributoRepository
                    .findByNomeIgnoreCase(nomeAtributo)
                    .orElseGet(() -> {

                        Atributo novo = new Atributo();

                        novo.setNome(nomeAtributo);

                        return atributoRepository.save(novo);
                    });

            // ==========================================
            // Buscar especificação existente
            // ==========================================

            Especificacao especificacao =
                    especificacaoRepository
                            .findByVehicleAndAtributo(
                                    vehicle,
                                    atributo
                            )
                            .orElseGet(() -> {

                                Especificacao nova =
                                        new Especificacao();

                                nova.setVehicle(vehicle);
                                nova.setAtributo(atributo);

                                return nova;
                            });

            // ==========================================
            // Atualizar valor
            // ==========================================

            especificacao.setValor(valor);

            especificacaoRepository.save(especificacao);
        });
    }

    // ======================================================
    // 📦 RESPONSE
    // ======================================================

    private VehicleCrudResponse montarResponse(
            Veiculo vehicle
    ) {

        Map<String, String> especificacoes =
                especificacaoRepository
                        .findByVehicleId(vehicle.getId())
                        .stream()
                        .collect(Collectors.toMap(
                                e -> e.getAtributo().getNome(),
                                Especificacao::getValor
                        ));

        return new VehicleCrudResponse(
                vehicle.getId(),
                vehicle.getMarca(),
                vehicle.getModelo(),
                vehicle.getVersao(),
                especificacoes
        );
    }



    // ======================================================
    // ❌ DELETE
    // ======================================================

    @Transactional
    public void deletar(Long id) throws NotFoundException {

        Veiculo vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado: " + id));

        especificacaoRepository.deleteByVehicleId(id);

        vehicleRepository.delete(vehicle);

        log.info("Veículo deletado: id={}", id);
    }

    // ======================================================
    // 📋 LISTAGEM
    // ======================================================

    @Transactional(readOnly = true)
    public Page<VehicleCrudResponse> listarPaginado(Pageable pageable) {

        return vehicleRepository.findAll(pageable)
                .map(v -> new VehicleCrudResponse(
                        v.getId(),
                        v.getMarca(),
                        v.getModelo(),
                        v.getVersao(),
                        null
                ));
    }

    @Transactional(readOnly = true)
    public VehicleCrudResponse buscarPorId(Long id) throws NotFoundException {
        Veiculo vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado: " + id));
        return montarResponse(vehicle);
    }

    // ======================================================
    // 🔒 MÉTODOS PRIVADOS
    // ======================================================

    private void validarDuplicidade(VehicleCreateRequest request) throws BadRequestException {

        boolean existe = vehicleRepository.existsByMarcaIgnoreCaseAndModeloIgnoreCaseAndVersaoIgnoreCase(
                request.marca(),
                request.modelo(),
                request.versao()
        );

        if (existe) {
            throw new BadRequestException(
                    "Já existe um veículo cadastrado com essa marca/modelo/versão"
            );
        }
    }

    private void salvarEspecificacoes(
            Veiculo vehicle,
            Map<String, String> especificacoes
    ) throws BadRequestException {

        Map<String, String> mapa =
                Optional.ofNullable(especificacoes)
                        .orElse(Collections.emptyMap());

        List<Especificacao> lista = new ArrayList<>();

        for (Map.Entry<String, String> entry : mapa.entrySet()) {

            String nome = normalizar(entry.getKey());

            Atributo atributo = atributoRepository
                    .findByNomeIgnoreCase(nome)
                    .orElseThrow(() -> new BadRequestException("Atributo não encontrado: " + nome));

            Especificacao especificacao = Especificacao.builder()
                    .vehicle(vehicle)
                    .atributo(atributo)
                    .valor(entry.getValue())
                    .build();

            lista.add(especificacao);
        }

        if (!lista.isEmpty()) {
            especificacaoRepository.saveAll(lista);
        }
    }

    private VehicleCrudResponse montarResponse(
            Veiculo vehicle,
            Map<String, String> especificacoes
    ) {

        return new VehicleCrudResponse(
                vehicle.getId(),
                vehicle.getMarca(),
                vehicle.getModelo(),
                vehicle.getVersao(),
                especificacoes
        );
    }

    private String normalizar(String valor) {
        return StringNormalizer.normalize(valor);
    }

    private List<String> normalizarLista(List<String> lista) {

        return Optional.ofNullable(lista)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::normalizar)
                .collect(Collectors.toList());
    }

    private String montarNomeVeiculo(VehicleRequest request) {

        return request.marca()
                + " "
                + request.modelo()
                + " "
                + request.versao();
    }
}
