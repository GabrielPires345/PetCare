package com.petcare.mapper.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ServicoRequest(
    @NotNull(message = "O campo nome é obrigatório")
    String nome,
    String descricao,
    @NotNull(message = "O campo preço base é obrigatório")
    BigDecimal precoBase,
    @NotNull(message = "O campo duração em minutos é obrigatório")
    Integer duracaoMinutos
) {
}
