package com.petcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "endereco")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_endereco")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "clinica_id")
    private Clinica clinica;

    @Column(name = "logradouro", nullable = false)
    @NotBlank
    private String logradouro;

    @Column(name = "numero", nullable = false)
    @NotBlank
    private String numero;

    @Column(name = "bairro", nullable = false)
    @NotBlank
    private String bairro;

    @Column(name = "cidade", nullable = false)
    @NotBlank
    private String cidade;

    @Column(name = "uf", nullable = false, length = 2)
    @NotBlank
    @Size(max = 2)
    private String uf;

    @Column(name = "cep", nullable = false)
    @NotBlank
    private String cep;
}