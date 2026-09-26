package com.automotiva.ficha_tecnica.entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "especificacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Especificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Veiculo vehicle;

    @ManyToOne
    @JoinColumn(name = "atributo_id", nullable = false)
    private Atributo atributo;

    private String valor;
}