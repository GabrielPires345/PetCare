package com.petcare.mapper.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PetRequest(
    @NotBlank(message = "O campo nome é obrigatório")
    String nome,

    @NotBlank(message = "O campo espécie é obrigatório")
    String especie,

    @NotBlank(message = "O campo sexo é obrigatório")
    String sexo,

    @NotNull(message = "O campo peso é obrigatório")
    Double peso,

    @NotNull(message = "O campo data de nascimento é obrigatório")
    LocalDate dataNascimento,

    @NotNull(message = "O campo castrado é obrigatório")
    Boolean castrado
) {
}
