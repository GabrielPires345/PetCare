package com.petcare.mapper.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ServicoResponse(
    UUID id,
    String nome,
    String descricao,
    BigDecimal precoBase,
    Integer duracaoMinutos
) {
}
