package com.petcare.mapper.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record TelefoneResponse(
    UUID id,
    String ddd,
    String numero,
    Boolean whatsapp
) {
}
