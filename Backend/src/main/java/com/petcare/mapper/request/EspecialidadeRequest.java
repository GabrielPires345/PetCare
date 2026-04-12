package com.petcare.mapper.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EspecialidadeRequest(
    @NotBlank(message = "O campo nome é obrigatório")
    String nome
) {
}