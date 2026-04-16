package com.petcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE cliente SET deleted_at = NOW() WHERE id_cliente = ?") // Intercepta o delete
@SQLRestriction("deleted_at IS NULL")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cliente")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "nome_completo", nullable = true)
    private String nomeCompleto;

    @Column(name = "cpf", nullable = true, unique = true)
    private String cpf;

    @Column(name = "data_nascimento", nullable = true)
    private LocalDate dataNascimento;

    @OneToMany(mappedBy = "cliente")
    private List<Endereco> enderecos;

    @OneToMany(mappedBy = "cliente")
    private List<Telefone> telefones;

    @OneToMany(mappedBy = "cliente")
    private List<Pet> pets = new ArrayList<>();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}