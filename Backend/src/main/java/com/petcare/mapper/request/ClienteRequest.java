package com.petcare.mapper.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Builder
public record ClienteRequest(
    @NotBlank(message = "O campo nome_completo é obrigatório")
    String nomeCompleto,

    @NotBlank(message = "O campo cpf é obrigatório")
    @CPF(message = "O campo cpf deve ser um CPF válido")
    String cpf,

    @Past(message = "A data de nascimento deve ser uma data no passado")
    LocalDate dataNascimento
) {
}