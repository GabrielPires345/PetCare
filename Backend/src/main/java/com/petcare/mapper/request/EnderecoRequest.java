package com.petcare.mapper.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record EnderecoRequest(
    @NotBlank(message = "O campo logradouro é obrigatório")
    String logradouro,

    @NotBlank(message = "O campo número é obrigatório")
    String numero,

    @NotBlank(message = "O campo bairro é obrigatório")
    String bairro,

    @NotBlank(message = "O campo cidade é obrigatório")
    String cidade,

    @NotBlank(message = "O campo estado é obrigatório")
    @Size(max = 2, message = "O campo estado deve ter no máximo 2 caracteres")
    String uf,

    @NotBlank(message = "O campo CEP é obrigatório")
    String cep
) {
}
