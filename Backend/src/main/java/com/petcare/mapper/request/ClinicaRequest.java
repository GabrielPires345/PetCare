package com.petcare.mapper.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record ClinicaRequest(
    @NotBlank(message = "O campo nome da clínica é obrigatório")
    String nomeClinica,

    @NotBlank(message = "O campo razão social é obrigatório")
    String razaoSocial,

    @NotBlank(message = "O campo CNPJ é obrigatório")
    String cnpj
) {
}
