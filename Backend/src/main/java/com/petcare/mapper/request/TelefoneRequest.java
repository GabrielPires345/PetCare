package com.petcare.mapper.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TelefoneRequest(
    @NotBlank(message = "O campo DDD é obrigatório")
    @Size(max = 2, message = "O campo DDD deve ter no máximo 2 caracteres")
    String ddd,

    @NotBlank(message = "O campo número é obrigatório")
    String numero,

    @NotNull(message = "O campo whatsapp é obrigatório")
    Boolean whatsapp
) {
}
