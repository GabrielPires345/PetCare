package com.petcare.repository;

import com.petcare.model.Especialidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.Set;

public interface EspecialidadeRepository extends JpaRepository<Especialidade, UUID> {
}