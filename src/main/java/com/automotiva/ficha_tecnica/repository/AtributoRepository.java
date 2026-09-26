package com.automotiva.ficha_tecnica.repository;



import com.automotiva.ficha_tecnica.entity.Atributo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AtributoRepository extends JpaRepository<Atributo, Long> {

    Optional<Atributo> findByNomeIgnoreCase(String nome);
}