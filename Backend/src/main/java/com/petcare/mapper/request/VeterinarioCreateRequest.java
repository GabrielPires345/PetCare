package com.petcare.mapper.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record VeterinarioCreateRequest(
    @NotBlank(message = "O campo nome de usuário é obrigatório")
    @Size(max = 10, message = "O nome de usuário deve ter no máximo 10 caracteres")
    String nomeUsuario,

    @NotBlank(message = "O campo email é obrigatório")
    String email,

    @NotBlank(message = "O campo senha é obrigatório")
    String senha,

    @NotBlank(message = "O campo confirmação de senha é obrigatório")
    String confirmaSenha,

    @NotBlank(message = "O campo nome é obrigatório")
    String nome,

    @NotBlank(message = "O campo CRMV é obrigatório")
    String crmv,

    Set<UUID> especialidadeIds
) {
}
