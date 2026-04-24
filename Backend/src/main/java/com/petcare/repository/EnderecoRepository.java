package com.petcare.repository;

import com.petcare.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {
    List<Endereco> findByCliente_Id(UUID clienteId);
    List<Endereco> findByClinica_Id(UUID clinicaId);
}