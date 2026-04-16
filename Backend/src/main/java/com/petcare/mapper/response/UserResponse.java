package com.petcare.mapper.response;

import lombok.Builder;
import java.util.UUID;

@Builder
public record UserResponse(
    UUID id,
    String nomeUsuario,
    String email,
    String nivelAcesso
) {
}
