package com.automotiva.ficha_tecnica.repository;

import com.automotiva.ficha_tecnica.entity.Atributo;
import com.automotiva.ficha_tecnica.entity.Especificacao;
import com.automotiva.ficha_tecnica.entity.Veiculo;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EspecificacaoRepository
        extends JpaRepository<Especificacao, Long> {

    @Query("""
        SELECT e FROM Especificacao e
        JOIN e.vehicle v
        JOIN e.atributo a
        WHERE v.marca = :marca
        AND v.modelo = :modelo
        AND v.versao = :versao
        AND a.nome IN :atributos
    """)
    List<Especificacao> buscarEspecificacoes(
            @Param("marca") String marca,
            @Param("modelo") String modelo,
            @Param("versao") String versao,
            @Param("atributos") List<String> atributos
    );

    List<Especificacao> findByVehicleId(Long vehicleId);

    void deleteByVehicleId(Long id);

    Optional<Especificacao> findByVehicleAndAtributo(
            Veiculo vehicle,
            Atributo atributo
    );
}

