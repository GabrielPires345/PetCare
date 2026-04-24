package com.petcare.mapper.response;

import com.petcare.model.Agendamento;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AgendamentoResponse(
    UUID id,
    String petName,
    String clinicaName,
    String veterinarioName,
    String servicoName,
    LocalDateTime dataHoraMarcada,
    String status,
    BigDecimal valorFinal
) {
}