package com.petcare.mapper.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AgendamentoRequest(
    @NotNull(message = "O ID do pet é obrigatório")
    UUID petId,
    @NotNull(message = "O ID da clínica é obrigatório")
    UUID clinicaId,
    @NotNull(message = "O ID do veterinário é obrigatório")
    UUID veterinarioId,
    @NotNull(message = "O ID do serviço é obrigatório")
    UUID servicoId,
    @NotNull(message = "A data e hora marcadas são obrigatórias")
    LocalDateTime dataHoraMarcada
) {
}
