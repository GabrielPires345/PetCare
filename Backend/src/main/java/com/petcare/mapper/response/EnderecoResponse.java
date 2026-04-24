package com.petcare.mapper.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record EnderecoResponse(
    UUID id,
    String logradouro,
    String numero,
    String bairro,
    String cidade,
    String uf,
    String cep
) {
}
