package com.petcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.br.CNPJ;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "clinica")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clinica {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_clinica")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "nome_clinica", nullable = false)
    @NotBlank
    private String nomeClinica;

    @Column(name = "razao_social", nullable = false)
    @NotBlank
    private String razaoSocial;

    @Column(name = "cnpj", nullable = false, unique = true)
    @CNPJ
    private String cnpj;

    @OneToMany(mappedBy = "clinica")
    private Set<Endereco> enderecos = new HashSet<>();

    @OneToMany(mappedBy = "clinica")
    private Set<Telefone> telefones = new HashSet<>();

    @OneToMany(mappedBy = "clinica")
    private Set<ClinicaVeterinario> clinicaVeterinarios = new HashSet<>();
}