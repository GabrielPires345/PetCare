package com.petcare.mapper;

import com.petcare.mapper.response.AgendamentoResponse;
import lombok.experimental.UtilityClass;
import com.petcare.model.Agendamento;
import com.petcare.model.Pet;
import com.petcare.model.Clinica;
import com.petcare.model.Veterinario;
import com.petcare.model.Servico;
import com.petcare.model.StatusAgendamento;
import com.petcare.mapper.request.AgendamentoRequest;

@UtilityClass
public class AgendamentoMapper {

    public static Agendamento toAgendamento(AgendamentoRequest request, Pet pet, Clinica clinica, Veterinario veterinario, Servico servico) {
        return Agendamento.builder()
                .pet(pet)
                .clinica(clinica)
                .veterinario(veterinario)
                .servico(servico)
                .dataHoraMarcada(request.dataHoraMarcada())
                .status(StatusAgendamento.AGENDADO)
                .valorFinal(servico.getPrecoBase() != null ? servico.getPrecoBase() : java.math.BigDecimal.ZERO)
                .build();
    }

    public static AgendamentoResponse toAgendamentoResponse(Agendamento agendamento) {
        return AgendamentoResponse.builder()
                .id(agendamento.getId())
                .petName(agendamento.getPet().getNome())
                .clinicaName(agendamento.getClinica().getNomeClinica())
                .veterinarioName(agendamento.getVeterinario().getNome())
                .servicoName(agendamento.getServico().getNome())
                .dataHoraMarcada(agendamento.getDataHoraMarcada())
                .status(agendamento.getStatus() != null ? agendamento.getStatus().name() : null)
                .valorFinal(agendamento.getValorFinal())
                .build();
    }
}
