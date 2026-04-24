package com.petcare.service;

import com.petcare.exception.RecursoDuplicadoException;
import com.petcare.exception.RecursoNaoEncontradoException;
import com.petcare.mapper.ClienteMapper;
import com.petcare.mapper.PetMapper;
import com.petcare.mapper.request.ClienteCreateRequest;
import com.petcare.mapper.request.ClienteUpdateRequest;
import com.petcare.mapper.request.PetRequest;
import com.petcare.mapper.response.ClienteResponse;
import com.petcare.model.Cliente;
import com.petcare.model.Pet;
import com.petcare.model.Usuario;
import com.petcare.repository.ClienteRepository;
import com.petcare.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ClienteService clienteService;

    private Usuario usuario;
    private ClienteCreateRequest clienteCreateRequest;
    private PetRequest petRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .email("cliente@email.com")
                .nomeUsuario("cliente1")
                .senhaHash("hash")
                .nivelAcesso("CLIENTE")
                .build();

        petRequest = new PetRequest(
                "Rex",
                "Cachorro",
                "Macho",
                5.0,
                LocalDate.of(2023, 1, 1),
                false
        );

        clienteCreateRequest = new ClienteCreateRequest(
                "user123",
                "cliente@email.com",
                "123456",
                "123456",
                "Joao Test",
                "12345678901",
                LocalDate.of(1990, 5, 15),
                petRequest
        );
    }

    // ==================== Testes de Registro ====================

    @Test
    @DisplayName("Deve registrar novo cliente com pet")
    void shouldRegisterNewClienteWithPet() {
        Cliente savedCliente = Cliente.builder()
                .id(UUID.randomUUID())
                .cpf(clienteCreateRequest.cpf())
                .nomeCompleto(clienteCreateRequest.nomeCompleto())
                .dataNascimento(clienteCreateRequest.dataNascimento())
                .usuario(usuario)
                .build();

        Pet savedPet = Pet.builder()
                .id(UUID.randomUUID())
                .nome("Rex")
                .especie("Cachorro")
                .build();

        when(clienteRepository.save(any(Cliente.class))).thenReturn(savedCliente);
        when(petRepository.save(any(Pet.class))).thenReturn(savedPet);

        ClienteResponse result = clienteService.registrarNovoCliente(clienteCreateRequest, usuario);

        assertNotNull(result);
        assertEquals("Joao Test", result.nomeCompleto());
        assertEquals("12345678901", result.cpf());
        verify(clienteRepository).save(any(Cliente.class));
        verify(petRepository).save(any(Pet.class));
    }

    // ==================== Testes de Busca ====================

    @Test
    @DisplayName("Deve retornar cliente por ID")
    void shouldReturnClienteById() {
        UUID id = UUID.randomUUID();
        Cliente cliente = Cliente.builder()
                .id(id)
                .nomeCompleto("Maria Silva")
                .cpf("11122233344")
                .dataNascimento(LocalDate.of(1985, 1, 1))
                .build();

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        ClienteResponse result = clienteService.getClienteById(id);

        assertEquals("Maria Silva", result.nomeCompleto());
        assertEquals("11122233344", result.cpf());
        verify(clienteRepository).findById(id);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando cliente não encontrado")
    void shouldThrowRecursoNaoEncontradoExceptionWhenClienteNotFoundById() {
        UUID id = UUID.randomUUID();
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> clienteService.getClienteById(id)
        );

        assertEquals("Cliente não encontrado", ex.getMessage());
        verify(clienteRepository).findById(id);
    }

    @Test
    @DisplayName("Deve retornar todos os clientes")
    void shouldReturnAllClientes() {
        Cliente c1 = Cliente.builder()
                .id(UUID.randomUUID())
                .nomeCompleto("Cliente A")
                .build();
        Cliente c2 = Cliente.builder()
                .id(UUID.randomUUID())
                .nomeCompleto("Cliente B")
                .build();

        when(clienteRepository.findAll()).thenReturn(List.of(c1, c2));

        List<ClienteResponse> result = clienteService.getAllClientes();

        assertEquals(2, result.size());
        verify(clienteRepository).findAll();
    }

    // ==================== Testes de Atualização ====================

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void shouldUpdateCliente() {
        UUID id = UUID.randomUUID();
        Cliente existing = Cliente.builder()
                .id(id)
                .nomeCompleto("Old Name")
                .cpf("11122233344")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .build();

        ClienteUpdateRequest request = new ClienteUpdateRequest(
                "New Name",
                "55566677788",
                LocalDate.of(1991, 2, 2)
        );

        when(clienteRepository.findById(id)).thenReturn(Optional.of(existing));
        when(clienteRepository.findByCpf("55566677788")).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClienteResponse result = clienteService.updateCliente(id, request);

        assertEquals("New Name", result.nomeCompleto());
        verify(clienteRepository).findById(id);
        verify(clienteRepository).findByCpf("55566677788");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar RecursoDuplicadoException quando atualizar com CPF duplicado")
    void shouldThrowRecursoDuplicadoExceptionWhenUpdatingWithDuplicateCpf() {
        UUID id = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();

        Cliente existing = Cliente.builder()
                .id(id)
                .nomeCompleto("Old Name")
                .cpf("11122233344")
                .build();

        Cliente otherClient = Cliente.builder()
                .id(otherId)
                .nomeCompleto("Other Client")
                .cpf("55566677788")
                .build();

        ClienteUpdateRequest request = new ClienteUpdateRequest(
                "Name",
                "55566677788",
                LocalDate.of(1990, 1, 1)
        );

        when(clienteRepository.findById(id)).thenReturn(Optional.of(existing));
        when(clienteRepository.findByCpf("55566677788")).thenReturn(Optional.of(otherClient));

        RecursoDuplicadoException ex = assertThrows(
                RecursoDuplicadoException.class,
                () -> clienteService.updateCliente(id, request)
        );

        assertEquals("CPF já cadastrado para outro cliente", ex.getMessage());
        verify(clienteRepository).findById(id);
        verify(clienteRepository).findByCpf("55566677788");
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve permitir atualizar cliente mantendo seu próprio CPF")
    void shouldAllowUpdatingWithOwnCpf() {
        UUID id = UUID.randomUUID();

        Cliente existing = Cliente.builder()
                .id(id)
                .nomeCompleto("Old")
                .cpf("11122233344")
                .build();

        ClienteUpdateRequest request = new ClienteUpdateRequest(
                "New",
                "11122233344", // mesmo CPF
                LocalDate.of(1990, 1, 1)
        );

        when(clienteRepository.findById(id)).thenReturn(Optional.of(existing));
        when(clienteRepository.findByCpf("11122233344")).thenReturn(Optional.of(existing));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClienteResponse result = clienteService.updateCliente(id, request);

        assertEquals("New", result.nomeCompleto());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao atualizar cliente inexistente")
    void shouldThrowRecursoNaoEncontradoExceptionWhenUpdatingNonExistentCliente() {
        UUID id = UUID.randomUUID();
        ClienteUpdateRequest request = new ClienteUpdateRequest("Name", "12345678901", LocalDate.of(1990, 1, 1));

        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> clienteService.updateCliente(id, request)
        );

        assertEquals("Cliente não encontrado", ex.getMessage());
        verify(clienteRepository).findById(id);
        verify(clienteRepository, never()).save(any());
    }

    // ==================== Testes de Deleção ====================

    @Test
    @DisplayName("Deve deletar cliente com sucesso")
    void shouldDeleteCliente() {
        UUID id = UUID.randomUUID();
        when(clienteRepository.existsById(id)).thenReturn(true);

        clienteService.deleteCliente(id);

        verify(clienteRepository).existsById(id);
        verify(clienteRepository).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao deletar cliente inexistente")
    void shouldThrowRecursoNaoEncontradoExceptionWhenDeletingNonExistentCliente() {
        UUID id = UUID.randomUUID();
        when(clienteRepository.existsById(id)).thenReturn(false);

        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> clienteService.deleteCliente(id)
        );

        assertEquals("Cliente não encontrado", ex.getMessage());
        verify(clienteRepository).existsById(id);
        verify(clienteRepository, never()).deleteById(any());
    }
}