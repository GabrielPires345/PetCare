package com.petcare.mapper.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ClinicaResponse(
    UUID id,
    String nomeClinica,
    String razaoSocial,
    String cnpj
) {
}
