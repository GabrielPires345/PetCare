package com.petcare.service;

import com.petcare.mapper.AgendamentoMapper;
import com.petcare.mapper.request.AgendamentoRequest;
import com.petcare.mapper.response.AgendamentoResponse;
import com.petcare.model.Agendamento;
import com.petcare.model.Clinica;
import com.petcare.model.Pet;
import com.petcare.model.Servico;
import com.petcare.model.StatusAgendamento;
import com.petcare.model.Veterinario;
import com.petcare.repository.AgendamentoRepository;
import com.petcare.repository.ClinicaRepository;
import com.petcare.repository.PetRepository;
import com.petcare.repository.ServicoRepository;
import com.petcare.repository.VeterinarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final PetRepository petRepository;
    private final ClinicaRepository clinicaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final ServicoRepository servicoRepository;

    @Transactional
    public AgendamentoResponse agendar(AgendamentoRequest request) {
        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new RuntimeException("Pet not found"));
        Clinica clinica = clinicaRepository.findById(request.clinicaId())
                .orElseThrow(() -> new RuntimeException("Clinica not found"));
        Veterinario veterinario = veterinarioRepository.findById(request.veterinarioId())
                .orElseThrow(() -> new RuntimeException("Veterinario not found"));
        Servico servico = servicoRepository.findById(request.servicoId())
                .orElseThrow(() -> new RuntimeException("Servico not found"));

        Agendamento agendamento = AgendamentoMapper.toAgendamento(request, pet, clinica, veterinario, servico);
        Agendamento saved = agendamentoRepository.save(agendamento);
        return AgendamentoMapper.toAgendamentoResponse(saved);
    }

    public AgendamentoResponse getById(UUID id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento not found"));
        return AgendamentoMapper.toAgendamentoResponse(agendamento);
    }

    public List<AgendamentoResponse> getByClienteId(UUID clienteId) {
        List<Agendamento> agendamentos = agendamentoRepository.findByPet_Cliente_Id(clienteId);
        return agendamentos.stream()
                .map(AgendamentoMapper::toAgendamentoResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelar(UUID id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento not found"));
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        agendamentoRepository.save(agendamento);
    }
}