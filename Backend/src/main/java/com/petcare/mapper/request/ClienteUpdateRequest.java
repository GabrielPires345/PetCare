package com.petcare.mapper.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Builder
public record ClienteUpdateRequest(
    @NotBlank(message = "O campo nome completo é obrigatório")
    String nomeCompleto,

    @NotBlank(message = "O campo CPF é obrigatório")
    @Size(min = 11, max = 14, message = "O campo CPF deve ter entre 11 e 14 caracteres")
    @CPF
    String cpf,

    @NotNull(message = "O campo data de nascimento é obrigatório")
    LocalDate dataNascimento
) {
}
