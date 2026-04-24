package com.petcare.service;

import com.petcare.model.*;
import com.petcare.mapper.request.AgendamentoRequest;
import com.petcare.mapper.response.AgendamentoResponse;
import com.petcare.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AgendamentoServiceTest {

    @Mock private AgendamentoRepository agendamentoRepository;
    @Mock private PetRepository petRepository;
    @Mock private ClinicaRepository clinicaRepository;
    @Mock private VeterinarioRepository veterinarioRepository;
    @Mock private ServicoRepository servicoRepository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private UUID petId, clinicaId, vetId, servicoId;
    private AgendamentoRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        petId = UUID.randomUUID();
        clinicaId = UUID.randomUUID();
        vetId = UUID.randomUUID();
        servicoId = UUID.randomUUID();

        request = new AgendamentoRequest(
                petId, clinicaId, vetId, servicoId, LocalDateTime.of(2026, 4, 15, 10, 0)
        );
    }

    @Test
    void shouldScheduleAppointmentWithValidEntities() {
        Pet pet = Pet.builder().id(petId).nome("Rex").build();
        Clinica clinica = Clinica.builder().id(clinicaId).nomeClinica("Vet Center").build();
        Veterinario vet = Veterinario.builder().id(vetId).nome("Dr Silva").build();
        Servico servico = Servico.builder().id(servicoId).nome("Consulta").precoBase(BigDecimal.valueOf(150)).build();

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(clinicaRepository.findById(clinicaId)).thenReturn(Optional.of(clinica));
        when(veterinarioRepository.findById(vetId)).thenReturn(Optional.of(vet));
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> {
            Agendamento a = invocation.getArgument(0);
            return Agendamento.builder().id(UUID.randomUUID()).pet(a.getPet()).clinica(a.getClinica())
                    .veterinario(a.getVeterinario()).servico(a.getServico()).dataHoraMarcada(a.getDataHoraMarcada())
                    .status(a.getStatus()).valorFinal(a.getValorFinal()).build();
        });

        AgendamentoResponse result = agendamentoService.agendar(request);

        assertNotNull(result);
        assertEquals("Rex", result.petName());
        assertEquals("Vet Center", result.clinicaName());
        assertEquals("Dr Silva", result.veterinarioName());
        assertEquals("Consulta", result.servicoName());
        assertEquals("AGENDADO", result.status());
        assertEquals(BigDecimal.valueOf(150), result.valorFinal());
        verify(agendamentoRepository).save(any(Agendamento.class));
    }

    @Test
    void shouldThrowWhenPetNotFound() {
        when(petRepository.findById(petId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> agendamentoService.agendar(request));
    }

    @Test
    void shouldThrowWhenClinicaNotFound() {
        when(petRepository.findById(petId)).thenReturn(Optional.of(Pet.builder().build()));
        when(clinicaRepository.findById(clinicaId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> agendamentoService.agendar(request));
    }

    @Test
    void shouldThrowWhenVeterinarioNotFound() {
        when(petRepository.findById(petId)).thenReturn(Optional.of(Pet.builder().build()));
        when(clinicaRepository.findById(clinicaId)).thenReturn(Optional.of(Clinica.builder().build()));
        when(veterinarioRepository.findById(vetId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> agendamentoService.agendar(request));
    }

    @Test
    void shouldThrowWhenServicoNotFound() {
        when(petRepository.findById(petId)).thenReturn(Optional.of(Pet.builder().build()));
        when(clinicaRepository.findById(clinicaId)).thenReturn(Optional.of(Clinica.builder().build()));
        when(veterinarioRepository.findById(vetId)).thenReturn(Optional.of(Veterinario.builder().build()));
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> agendamentoService.agendar(request));
    }

    @Test
    void shouldReturnAgendamentoById() {
        UUID id = UUID.randomUUID();
        Servico servico = Servico.builder().nome("Banho").precoBase(BigDecimal.valueOf(80)).build();
        Agendamento agendamento = Agendamento.builder()
                .id(id).dataHoraMarcada(LocalDateTime.now()).status(StatusAgendamento.AGENDADO)
                .pet(Pet.builder().nome("Rex").build()).clinica(Clinica.builder().nomeClinica("Clinica A").build())
                .veterinario(Veterinario.builder().nome("Dr Vet").build()).servico(servico).valorFinal(BigDecimal.valueOf(80)).build();
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));

        AgendamentoResponse result = agendamentoService.getById(id);

        assertEquals("Rex", result.petName());
        assertEquals(BigDecimal.valueOf(80), result.valorFinal());
    }

    @Test
    void shouldThrowWhenAgendamentoNotFound() {
        when(agendamentoRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> agendamentoService.getById(UUID.randomUUID()));
    }

    @Test
    void shouldReturnAgendamentosByClienteId() {
        UUID clienteId = UUID.randomUUID();
        Servico servico = Servico.builder().nome("Consulta").precoBase(BigDecimal.valueOf(100)).build();
        Agendamento a = Agendamento.builder()
                .pet(Pet.builder().nome("Rex").build()).clinica(Clinica.builder().nomeClinica("C").build())
                .veterinario(Veterinario.builder().nome("V").build()).servico(servico)
                .dataHoraMarcada(LocalDateTime.now()).status(StatusAgendamento.AGENDADO).valorFinal(BigDecimal.valueOf(100)).build();
        when(agendamentoRepository.findByPet_Cliente_Id(clienteId)).thenReturn(List.of(a));

        List<AgendamentoResponse> result = agendamentoService.getByClienteId(clienteId);

        assertEquals(1, result.size());
        assertEquals("Rex", result.get(0).petName());
    }

    @Test
    void shouldCancelAgendamento() {
        UUID id = UUID.randomUUID();
        Agendamento agendamento = Agendamento.builder()
                .id(id).status(StatusAgendamento.AGENDADO).build();
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));

        agendamentoService.cancelar(id);

        assertEquals(StatusAgendamento.CANCELADO, agendamento.getStatus());
        verify(agendamentoRepository).save(agendamento);
    }

    @Test
    void shouldThrowWhenCancelingNonExistentAgendamento() {
        when(agendamentoRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> agendamentoService.cancelar(UUID.randomUUID()));
    }
}
