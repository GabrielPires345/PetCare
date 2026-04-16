package com.petcare.repository;

import com.petcare.model.Clinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClinicaRepository extends JpaRepository<Clinica, UUID> {
    Optional<Clinica> findByCnpj(String cnpj);
}