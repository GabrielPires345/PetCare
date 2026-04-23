package com.petcare.service;

import com.petcare.mapper.request.ClienteCreateRequest;
import com.petcare.mapper.request.PetRequest;
import com.petcare.mapper.request.UserRequest;
import com.petcare.mapper.response.UserResponse;
import com.petcare.model.Usuario;
import com.petcare.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;
    @InjectMocks private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExistsByEmail_WhenEmailExists() {
        when(usuarioRepository.findByEmail("test@email.com")).thenReturn(Optional.of(new Usuario()));
        assertTrue(usuarioService.existsByEmail("test@email.com"));
    }

    @Test
    void testExistsByEmail_WhenEmailNotExists() {
        when(usuarioRepository.findByEmail("test@email.com")).thenReturn(Optional.empty());
        assertFalse(usuarioService.existsByEmail("test@email.com"));
    }

    @Test
    void testCriarUsuarioParaNovoCliente_Success() {
        PetRequest petRequest = new PetRequest("Rex", "Cachorro", "Macho", 5.0, LocalDate.of(2023, 1, 1), false);
        ClienteCreateRequest dto = new ClienteCreateRequest(
            "user123", "test@email.com", "123456", "123456",
            "Full Name", "12345678901", LocalDate.of(2000, 1, 1), petRequest
        );

        when(usuarioRepository.findByEmail("test@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            return Usuario.builder()
                .id(UUID.randomUUID()).nomeUsuario(u.getNomeUsuario()).email(u.getEmail())
                .senhaHash(u.getSenhaHash()).nivelAcesso(u.getNivelAcesso()).build();
        });

        Usuario result = usuarioService.criarUsuarioParaNovoCliente(dto);

        assertEquals("test@email.com", result.getEmail());
        assertEquals("CLIENTE", result.getNivelAcesso());
        assertEquals("hashedPassword", result.getSenhaHash());
        assertEquals("user123", result.getNomeUsuario());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testCriarUsuarioParaNovoCliente_ThrowsWhenEmailExists() {
        PetRequest petRequest = new PetRequest("Rex", "Cachorro", "Macho", 5.0, LocalDate.of(2023, 1, 1), false);
        ClienteCreateRequest dto = new ClienteCreateRequest(
            "user123", "existing@email.com", "123456", "123456",
            "Full Name", "12345678901", LocalDate.of(2000, 1, 1), petRequest
        );

        when(usuarioRepository.findByEmail("existing@email.com")).thenReturn(Optional.of(new Usuario()));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            usuarioService.criarUsuarioParaNovoCliente(dto));

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testGetUsuarioById_Success() {
        UUID id = UUID.randomUUID();
        Usuario usuario = Usuario.builder().id(id).email("test@email.com").nomeUsuario("testuser").senhaHash("hash").nivelAcesso("CLIENTE").build();
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        UserResponse result = usuarioService.getUsuarioById(id);

        assertEquals("test@email.com", result.email());
        assertEquals("testuser", result.nomeUsuario());
        assertEquals("CLIENTE", result.nivelAcesso());
    }

    @Test
    void testGetUsuarioById_ThrowsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usuarioService.getUsuarioById(id));
    }

    @Test
    void testGetAllUsuarios() {
        List<Usuario> usuarios = List.of(
            Usuario.builder().id(UUID.randomUUID()).email("a@email.com").nomeUsuario("user1").senhaHash("h").nivelAcesso("CLIENTE").build(),
            Usuario.builder().id(UUID.randomUUID()).email("b@email.com").nomeUsuario("user2").senhaHash("h").nivelAcesso("VET").build(),
            Usuario.builder().id(UUID.randomUUID()).email("c@email.com").nomeUsuario("user3").senhaHash("h").nivelAcesso("CLINICA").build()
        );
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<UserResponse> result = usuarioService.getAllUsuarios();

        assertEquals(3, result.size());
    }

    @Test
    void testUpdateUsuario_Success() {
        UUID id = UUID.randomUUID();
        Usuario existing = Usuario.builder().id(id).email("old@email.com").nomeUsuario("testuser").senhaHash("hash").nivelAcesso("CLIENTE").build();
        UserRequest request = new UserRequest("new@email.com", null, "ADMIN");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existing));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = usuarioService.updateUsuario(id, request);

        assertEquals("new@email.com", result.email());
        assertEquals("ADMIN", result.nivelAcesso());
        verify(usuarioRepository).save(existing);
    }

    @Test
    void testUpdateUsuario_ThrowsWhenNotFound() {
        UUID id = UUID.randomUUID();
        UserRequest request = new UserRequest("test@email.com", null, "ADMIN");
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usuarioService.updateUsuario(id, request));
    }

    @Test
    void testDeleteUsuario_Success() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.existsById(id)).thenReturn(true);

        usuarioService.deleteUsuario(id);

        verify(usuarioRepository).deleteById(id);
    }

    @Test
    void testDeleteUsuario_ThrowsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> usuarioService.deleteUsuario(id));
        verify(usuarioRepository, never()).deleteById(any());
    }
}
