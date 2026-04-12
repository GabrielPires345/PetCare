package com.petcare.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "especialidade")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Especialidade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_especialidade")
    private UUID id;

    @Column(name = "nome", nullable = false, unique = true)
    private String nome;

    @ManyToMany(mappedBy = "especialidades")
    private Set<Veterinario> veterinarios = new HashSet<>();
}