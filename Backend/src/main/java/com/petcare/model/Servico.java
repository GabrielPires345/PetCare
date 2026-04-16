package com.petcare.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "servico")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_servico")
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "preco_base", nullable = false)
    private BigDecimal precoBase;

    @Column(name = "duracao_minutos", nullable = false)
    private Integer duracaoMinutos;
}