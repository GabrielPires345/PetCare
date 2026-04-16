package com.petcare.service;

import com.petcare.mapper.TelefoneMapper;
import com.petcare.mapper.request.TelefoneRequest;
import com.petcare.mapper.response.TelefoneResponse;
import com.petcare.model.Cliente;
import com.petcare.model.Clinica;
import com.petcare.model.Telefone;
import com.petcare.repository.ClienteRepository;
import com.petcare.repository.ClinicaRepository;
import com.petcare.repository.TelefoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TelefoneServiceTest {

    @Mock private TelefoneRepository telefoneRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private ClinicaRepository clinicaRepository;

    @InjectMocks
    private TelefoneService telefoneService;

    private TelefoneRequest telefoneRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        telefoneRequest = new TelefoneRequest("11", "999999999", true);
    }

    @Test
    void shouldCreateTelefoneForCliente() {
        UUID clienteId = UUID.randomUUID();
        Cliente cliente = Cliente.builder().id(clienteId).build();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(telefoneRepository.save(any(Telefone.class))).thenAnswer(inv -> {
            Telefone t = inv.getArgument(0);
            return Telefone.builder().id(UUID.randomUUID()).ddd(t.getDdd()).numero(t.getNumero()).whatsapp(t.getWhatsapp()).build();
        });

        TelefoneResponse result = telefoneService.criarTelefoneParaCliente(clienteId, telefoneRequest);

        assertNotNull(result);
        assertEquals("11", result.ddd());
        assertEquals("999999999", result.numero());
        assertTrue(result.whatsapp());
    }

    @Test
    void shouldThrowWhenClienteNotFoundForTelefone() {
        UUID clienteId = UUID.randomUUID();
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> telefoneService.criarTelefoneParaCliente(clienteId, telefoneRequest));
    }

    @Test
    void shouldCreateTelefoneForClinica() {
        UUID clinicaId = UUID.randomUUID();
        Clinica clinica = Clinica.builder().id(clinicaId).build();

        when(clinicaRepository.findById(clinicaId)).thenReturn(Optional.of(clinica));
        when(telefoneRepository.save(any(Telefone.class))).thenAnswer(inv -> {
            Telefone t = inv.getArgument(0);
            return Telefone.builder().id(UUID.randomUUID()).ddd(t.getDdd()).numero(t.getNumero()).whatsapp(t.getWhatsapp()).build();
        });

        TelefoneResponse result = telefoneService.criarTelefoneParaClinica(clinicaId, telefoneRequest);

        assertNotNull(result);
        assertEquals("11", result.ddd());
    }

    @Test
    void shouldThrowWhenClinicaNotFoundForTelefone() {
        UUID clinicaId = UUID.randomUUID();
        when(clinicaRepository.findById(clinicaId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> telefoneService.criarTelefoneParaClinica(clinicaId, telefoneRequest));
    }

    @Test
    void shouldReturnTelefoneById() {
        UUID id = UUID.randomUUID();
        Telefone telefone = Telefone.builder().id(id).ddd("21").numero("888888888").whatsapp(false).build();
        when(telefoneRepository.findById(id)).thenReturn(Optional.of(telefone));

        TelefoneResponse result = telefoneService.getTelefoneById(id);

        assertEquals("21", result.ddd());
        assertEquals("888888888", result.numero());
        assertFalse(result.whatsapp());
    }

    @Test
    void shouldThrowWhenTelefoneNotFound() {
        when(telefoneRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> telefoneService.getTelefoneById(UUID.randomUUID()));
    }

    @Test
    void shouldReturnAllTelefones() {
        Telefone t1 = Telefone.builder().id(UUID.randomUUID()).ddd("11").build();
        Telefone t2 = Telefone.builder().id(UUID.randomUUID()).ddd("21").build();
        when(telefoneRepository.findAll()).thenReturn(List.of(t1, t2));

        List<TelefoneResponse> result = telefoneService.getAllTelefones();

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnTelefonesByClienteId() {
        UUID clienteId = UUID.randomUUID();
        Telefone t = Telefone.builder().id(UUID.randomUUID()).ddd("11").numero("999").build();
        when(telefoneRepository.findByCliente_Id(clienteId)).thenReturn(List.of(t));

        List<TelefoneResponse> result = telefoneService.getTelefonesByClienteId(clienteId);

        assertEquals(1, result.size());
        assertEquals("999", result.get(0).numero());
    }

    @Test
    void shouldReturnTelefonesByClinicaId() {
        UUID clinicaId = UUID.randomUUID();
        Telefone t = Telefone.builder().id(UUID.randomUUID()).ddd("31").build();
        when(telefoneRepository.findByClinica_Id(clinicaId)).thenReturn(List.of(t));

        List<TelefoneResponse> result = telefoneService.getTelefonesByClinicaId(clinicaId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldUpdateTelefone() {
        UUID id = UUID.randomUUID();
        Telefone existing = Telefone.builder().id(id).ddd("11").numero("000").whatsapp(false).build();
        TelefoneRequest request = new TelefoneRequest("21", "888888888", true);
        when(telefoneRepository.findById(id)).thenReturn(Optional.of(existing));
        when(telefoneRepository.save(any(Telefone.class))).thenAnswer(inv -> inv.getArgument(0));

        TelefoneResponse result = telefoneService.atualizarTelefone(id, request);

        assertEquals("21", result.ddd());
        assertEquals("888888888", result.numero());
        assertTrue(result.whatsapp());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentTelefone() {
        when(telefoneRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> telefoneService.atualizarTelefone(UUID.randomUUID(), telefoneRequest));
    }

    @Test
    void shouldDeleteTelefone() {
        UUID id = UUID.randomUUID();
        when(telefoneRepository.existsById(id)).thenReturn(true);

        telefoneService.deletarTelefone(id);

        verify(telefoneRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentTelefone() {
        when(telefoneRepository.existsById(any())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> telefoneService.deletarTelefone(UUID.randomUUID()));
    }
}
