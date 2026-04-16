package com.petcare.mapper.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record PetResponse(
    UUID id,
    String nome,
    String especie,
    String sexo,
    Double peso,
    LocalDate dataNascimento,
    Boolean castrado
) {
}
