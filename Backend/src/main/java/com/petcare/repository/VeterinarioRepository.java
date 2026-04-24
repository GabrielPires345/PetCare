package com.petcare.repository;

import com.petcare.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, UUID> {
    Optional<Veterinario> findByCrmv(String crmv);
    Optional<Veterinario> findByUsuarioId(UUID usuarioId);
}