package com.petcare.mapper.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserRequest(
    @NotBlank(message = "O campo email é obrigatório")
    @Email(message = "O campo email deve ter formato de email.")
    String email,

    @NotBlank(message = "O campo password é obrigatório")
    String password,

    @NotBlank(message = "O campo nivel_acesso é obrigatório")
    String nivelAcesso
) {
}