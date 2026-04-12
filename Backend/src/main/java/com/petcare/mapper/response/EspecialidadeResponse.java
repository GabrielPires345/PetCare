package com.petcare.mapper.response;

import lombok.Builder;
import java.util.UUID;

@Builder
public record EspecialidadeResponse(
    UUID id,
    String nome
) {
}