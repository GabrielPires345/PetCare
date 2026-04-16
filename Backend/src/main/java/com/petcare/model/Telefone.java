package com.petcare.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "telefone")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Telefone {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_telefone")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "clinica_id")
    private Clinica clinica;

    @Column(name = "ddd", nullable = false, length = 2)
    private String ddd;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Column(name = "whatsapp", nullable = false)
    private Boolean whatsapp;
}