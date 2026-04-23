package com.petcare.mapper.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.hibernate.validator.constraints.br.CNPJ;

@Builder
public record ClinicaCreateRequest(
    @NotBlank(message = "O campo nome de usuário é obrigatório")
    @Size(max = 10, message = "O nome de usuário deve ter no máximo 10 caracteres")
    String nomeUsuario,

    @NotBlank(message = "O campo email é obrigatório")
    String email,

    @NotBlank(message = "O campo senha é obrigatório")
    String senha,

    @NotBlank(message = "O campo confirmação de senha é obrigatório")
    String confirmaSenha,

    @NotBlank(message = "O campo nome da clínica é obrigatório")
    String nomeClinica,

    @NotBlank(message = "O campo razão social é obrigatório")
    String razaoSocial,

    @NotBlank(message = "O campo CNPJ é obrigatório")
    @CNPJ
    String cnpj
) {
}
