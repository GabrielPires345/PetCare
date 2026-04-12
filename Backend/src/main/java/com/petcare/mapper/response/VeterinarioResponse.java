package com.petcare.mapper.response;

import lombok.Builder;
import java.util.Set;
import java.util.UUID;

@Builder
public record VeterinarioResponse(
    UUID id,
    String nome,
    String crmv,
    Set<UUID> especialidadeIds
) {
}