package com.petcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cliente")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "nome_completo", nullable = false)
    @NotBlank
    private String nomeCompleto;

    @Column(name = "cpf", nullable = false, unique = true)
    @CPF
    private String cpf;

    @Column(name = "data_nascimento", nullable = false)
    @Past
    private LocalDate dataNascimento;

    @OneToMany(mappedBy = "cliente")
    private java.util.List<Endereco> enderecos;

    @OneToMany(mappedBy = "cliente")
    private java.util.List<Telefone> telefones;

    @OneToMany(mappedBy = "cliente")
    private java.util.List<Pet> pets;
}