package com.petcare.mapper.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record VeterinarioRequest(
    @NotBlank(message = "O campo nome é obrigatório")
    String nome,

    @NotBlank(message = "O campo crmv é obrigatório")
    String crmv,

    Set<UUID> especialidadeIds
) {
}