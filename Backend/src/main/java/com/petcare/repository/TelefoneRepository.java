package com.petcare.repository;

import com.petcare.model.Telefone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TelefoneRepository extends JpaRepository<Telefone, UUID> {
    List<Telefone> findByCliente_Id(UUID clienteId);
    List<Telefone> findByClinica_Id(UUID clinicaId);
}