package com.petcare.mapper.response;

import lombok.Builder;
import java.util.UUID;
import java.time.LocalDate;

@Builder
public record ClienteResponse(
    UUID id,
    String nomeCompleto,
    String cpf,
    LocalDate dataNascimento
) {
}
