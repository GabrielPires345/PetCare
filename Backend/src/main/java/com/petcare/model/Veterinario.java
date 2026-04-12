package com.petcare.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "veterinario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_veterinario")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "crmv", nullable = false, unique = true)
    private String crmv;

    @ManyToMany
    @JoinTable(
        name = "veterinario_especialidade",
        joinColumns = @JoinColumn(name = "veterinario_id"),
        inverseJoinColumns = @JoinColumn(name = "especialidade_id")
    )
    private Set<Especialidade> especialidades = new HashSet<>();
}