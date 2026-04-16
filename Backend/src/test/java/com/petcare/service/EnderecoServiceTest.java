package com.petcare.service;

import com.petcare.mapper.EnderecoMapper;
import com.petcare.mapper.request.EnderecoRequest;
import com.petcare.mapper.response.EnderecoResponse;
import com.petcare.model.Cliente;
import com.petcare.model.Clinica;
import com.petcare.model.Endereco;
import com.petcare.repository.ClienteRepository;
import com.petcare.repository.ClinicaRepository;
import com.petcare.repository.EnderecoRepository;
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

class EnderecoServiceTest {

    @Mock private EnderecoRepository enderecoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private ClinicaRepository clinicaRepository;

    @InjectMocks
    private EnderecoService enderecoService;

    private EnderecoRequest enderecoRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        enderecoRequest = new EnderecoRequest("Rua A", "100", "Centro", "Sao Paulo", "SP", "01000000");
    }

    @Test
    void shouldCreateEnderecoForCliente() {
        UUID clienteId = UUID.randomUUID();
        Cliente cliente = Cliente.builder().id(clienteId).build();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(inv -> {
            Endereco e = inv.getArgument(0);
            return Endereco.builder().id(UUID.randomUUID()).logradouro(e.getLogradouro()).numero(e.getNumero()).bairro(e.getBairro()).cidade(e.getCidade()).uf(e.getUf()).cep(e.getCep()).build();
        });

        EnderecoResponse result = enderecoService.criarEnderecoParaCliente(clienteId, enderecoRequest);

        assertNotNull(result);
        assertEquals("Rua A", result.logradouro());
        assertEquals("100", result.numero());
        assertEquals("Centro", result.bairro());
        assertEquals("SP", result.uf());
    }

    @Test
    void shouldThrowWhenClienteNotFoundForEndereco() {
        UUID clienteId = UUID.randomUUID();
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> enderecoService.criarEnderecoParaCliente(clienteId, enderecoRequest));
    }

    @Test
    void shouldCreateEnderecoForClinica() {
        UUID clinicaId = UUID.randomUUID();
        Clinica clinica = Clinica.builder().id(clinicaId).build();

        when(clinicaRepository.findById(clinicaId)).thenReturn(Optional.of(clinica));
        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(inv -> {
            Endereco e = inv.getArgument(0);
            return Endereco.builder().id(UUID.randomUUID()).logradouro(e.getLogradouro()).numero(e.getNumero()).build();
        });

        EnderecoResponse result = enderecoService.criarEnderecoParaClinica(clinicaId, enderecoRequest);

        assertNotNull(result);
        assertEquals("Rua A", result.logradouro());
    }

    @Test
    void shouldThrowWhenClinicaNotFoundForEndereco() {
        UUID clinicaId = UUID.randomUUID();
        when(clinicaRepository.findById(clinicaId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> enderecoService.criarEnderecoParaClinica(clinicaId, enderecoRequest));
    }

    @Test
    void shouldReturnEnderecoById() {
        UUID id = UUID.randomUUID();
        Endereco endereco = Endereco.builder().id(id).logradouro("Rua B").numero("200").bairro("Bairro").cidade("Cidade").uf("RJ").cep("12345678").build();
        when(enderecoRepository.findById(id)).thenReturn(Optional.of(endereco));

        EnderecoResponse result = enderecoService.getEnderecoById(id);

        assertEquals("Rua B", result.logradouro());
        assertEquals("200", result.numero());
    }

    @Test
    void shouldThrowWhenEnderecoNotFound() {
        when(enderecoRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> enderecoService.getEnderecoById(UUID.randomUUID()));
    }

    @Test
    void shouldReturnAllEnderecos() {
        Endereco e1 = Endereco.builder().id(UUID.randomUUID()).logradouro("A").build();
        Endereco e2 = Endereco.builder().id(UUID.randomUUID()).logradouro("B").build();
        when(enderecoRepository.findAll()).thenReturn(List.of(e1, e2));

        List<EnderecoResponse> result = enderecoService.getAllEnderecos();

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEnderecosByClienteId() {
        UUID clienteId = UUID.randomUUID();
        Endereco e = Endereco.builder().id(UUID.randomUUID()).logradouro("Rua Cliente").build();
        when(enderecoRepository.findByCliente_Id(clienteId)).thenReturn(List.of(e));

        List<EnderecoResponse> result = enderecoService.getEnderecosByClienteId(clienteId);

        assertEquals(1, result.size());
        assertEquals("Rua Cliente", result.get(0).logradouro());
    }

    @Test
    void shouldReturnEnderecosByClinicaId() {
        UUID clinicaId = UUID.randomUUID();
        Endereco e = Endereco.builder().id(UUID.randomUUID()).logradouro("Rua Clinica").build();
        when(enderecoRepository.findByClinica_Id(clinicaId)).thenReturn(List.of(e));

        List<EnderecoResponse> result = enderecoService.getEnderecosByClinicaId(clinicaId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldUpdateEndereco() {
        UUID id = UUID.randomUUID();
        Endereco existing = Endereco.builder().id(id).logradouro("Old").numero("0").bairro("Old").cidade("Old").uf("SP").cep("0").build();
        EnderecoRequest request = new EnderecoRequest("New Street", "500", "New Neighborhood", "New City", "RJ", "99999999");
        when(enderecoRepository.findById(id)).thenReturn(Optional.of(existing));
        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(inv -> inv.getArgument(0));

        EnderecoResponse result = enderecoService.atualizarEndereco(id, request);

        assertEquals("New Street", result.logradouro());
        assertEquals("500", result.numero());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentEndereco() {
        when(enderecoRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> enderecoService.atualizarEndereco(UUID.randomUUID(), enderecoRequest));
    }

    @Test
    void shouldDeleteEndereco() {
        UUID id = UUID.randomUUID();
        when(enderecoRepository.existsById(id)).thenReturn(true);

        enderecoService.deletarEndereco(id);

        verify(enderecoRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentEndereco() {
        when(enderecoRepository.existsById(any())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> enderecoService.deletarEndereco(UUID.randomUUID()));
    }
}
