package com.petcare.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE pet SET deleted_at = NOW() WHERE id_pet = ?")
@SQLRestriction("deleted_at IS NULL")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_pet")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "especie", nullable = false)
    private String especie;

    @Column(name = "sexo", nullable = false)
    private String sexo;

    @Column(name = "peso", nullable = false)
    private Double peso;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "castrado", nullable = false)
    private Boolean castrado;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}