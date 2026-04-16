package com.petcare.mapper.request;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import lombok.Builder;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Builder
public record ClienteCreateRequest(
    @NotBlank(message = "O campo nome de usuário é obrigatório")
    @Size(max = 10, message = "O campo nome de usuário deve ter no máximo 10 caracteres")
    String nomeUsuario,

    @NotBlank(message = "O campo email é obrigatório")
    @Email(message = "O campo email deve ter um formato válido")
    String email,

    @NotBlank(message = "O campo senha é obrigatório")
    String senha,

    @NotBlank(message = "O campo confirmação de senha é obrigatório")
    String confirmaSenha,

    @NotBlank(message = "O campo nome completo é obrigatório")
    String nomeCompleto,

    @NotBlank(message = "O campo CPF é obrigatório")
    @Size(min = 11, max = 14, message = "O campo CPF deve ter entre 11 e 14 caracteres")
    @CPF
    String cpf,

    @NotNull(message = "O campo data de nascimento é obrigatório")
    LocalDate dataNascimento,

    @Valid
    PetRequest pet
) {
}
