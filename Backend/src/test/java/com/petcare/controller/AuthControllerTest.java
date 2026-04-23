package com.petcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petcare.exception.AutenticacaoException;
import com.petcare.exception.ErrorCode;
import com.petcare.exception.RecursoDuplicadoException;
import com.petcare.exception.RecursoNaoEncontradoException;
import com.petcare.mapper.request.ClienteCreateRequest;
import com.petcare.mapper.request.LoginRequest;
import com.petcare.mapper.request.PetRequest;
import com.petcare.model.Cliente;
import com.petcare.model.Usuario;
import com.petcare.repository.ClienteRepository;
import com.petcare.repository.UsuarioRepository;
import com.petcare.security.JwtUtil;
import com.petcare.service.ClienteService;
import com.petcare.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ClienteService clienteService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private Usuario usuario;
    private Cliente cliente;
    private ClienteCreateRequest clienteCreateRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .email("teste@email.com")
                .nomeUsuario("testeuser")
                .senhaHash("hash")
                .nivelAcesso("CLIENTE")
                .emailVerificado(true)
                .build();

        cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .usuario(usuario)
                .nomeCompleto("Teste Usuario")
                .cpf("12345678901")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .build();

        PetRequest petRequest = new PetRequest(
                "Rex",
                "Cachorro",
                "Macho",
                10.0,
                LocalDate.of(2020, 1, 1),
                false
        );

        clienteCreateRequest = new ClienteCreateRequest(
                "testeuser",
                "teste@email.com",
                "senha123",
                "senha123",
                "Teste Usuario",
                "12345678901",
                LocalDate.of(1990, 1, 1),
                petRequest
        );

        loginRequest = new LoginRequest("teste@email.com", "senha123");
    }

    // ==================== Registro ====================

    @Test
    @DisplayName("Deve registrar cliente com sucesso quando senhas conferem")
    void deveRegistrarClienteComSucesso() {
        when(usuarioService.criarUsuarioParaNovoCliente(any(ClienteCreateRequest.class))).thenReturn(usuario);

        ResponseEntity<Map<String, Object>> response = authController.registrarCliente(clienteCreateRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("mensagem"));
        assertTrue(response.getBody().containsKey("email"));

        verify(usuarioService).criarUsuarioParaNovoCliente(any(ClienteCreateRequest.class));
        verify(clienteService).registrarNovoCliente(any(ClienteCreateRequest.class), any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar AutenticacaoException quando senhas não conferem")
    void deveLancarAutenticacaoExceptionQuandoSenhasNaoConferem() {
        ClienteCreateRequest requestSenhasDiferentes = new ClienteCreateRequest(
                "testeuser",
                "teste@email.com",
                "senha123",
                "senha456", // senha diferente
                "Teste Usuario",
                "12345678901",
                LocalDate.of(1990, 1, 1),
                null
        );

        AutenticacaoException ex = assertThrows(
                AutenticacaoException.class,
                () -> authController.registrarCliente(requestSenhasDiferentes)
        );

        assertEquals(ErrorCode.SENHAS_NAO_CONFEREM, ex.getErrorCode());
        verify(usuarioService, never()).criarUsuarioParaNovoCliente(any());
    }

    @Test
    @DisplayName("Deve propagar RecursoDuplicadoException quando email já cadastrado")
    void devePropagarRecursoDuplicadoExceptionQuandoEmailJaCadastrado() {
        when(usuarioService.criarUsuarioParaNovoCliente(any(ClienteCreateRequest.class)))
                .thenThrow(new RecursoDuplicadoException("Email já cadastrado"));

        assertThrows(
                RecursoDuplicadoException.class,
                () -> authController.registrarCliente(clienteCreateRequest)
        );

        verify(clienteService, never()).registrarNovoCliente(any(), any());
    }

    @Test
    @DisplayName("Deve lançar AutenticacaoException quando login com email não verificado")
    void deveLancarAutenticacaoExceptionQuandoEmailNaoVerificado() {
        Usuario usuarioNaoVerificado = Usuario.builder()
                .id(UUID.randomUUID())
                .email("naoVerificado@email.com")
                .nomeUsuario("user")
                .senhaHash("hash")
                .nivelAcesso("CLIENTE")
                .emailVerificado(false)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(usuarioRepository.findByEmail(any(String.class))).thenReturn(Optional.of(usuarioNaoVerificado));

        LoginRequest loginNaoVerificado = new LoginRequest("naoVerificado@email.com", "senha123");

        AutenticacaoException ex = assertThrows(
                AutenticacaoException.class,
                () -> authController.fazerLogin(loginNaoVerificado)
        );

        assertEquals(ErrorCode.EMAIL_NAO_VERIFICADO, ex.getErrorCode());
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    // ==================== Login ====================

    @Test
    @DisplayName("Deve fazer login com sucesso")
    void deveFazerLoginComSucesso() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(usuarioRepository.findByEmail(any(String.class))).thenReturn(Optional.of(usuario));
        when(clienteRepository.findByUsuarioId(any(UUID.class))).thenReturn(Optional.of(cliente));
        when(jwtUtil.generateToken(any(UUID.class), any(String.class))).thenReturn("token-jwt");

        ResponseEntity<Map<String, Object>> response = authController.fazerLogin(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("token"));
        assertTrue(response.getBody().containsKey("user"));
        assertTrue(response.getBody().containsKey("clienteId"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository).findByEmail(loginRequest.email());
        verify(clienteRepository).findByUsuarioId(usuario.getId());
    }

    @Test
    @DisplayName("Deve propagar BadCredentialsException quando credenciais inválidas")
    void devePropagarBadCredentialsExceptionQuandoCredenciaisValidas() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> authController.fazerLogin(loginRequest)
        );

        verify(usuarioRepository, never()).findByEmail(any());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando usuário não encontrado após autenticação")
    void deveLancarRecursoNaoEncontradoExceptionQuandoUsuarioNaoEncontrado() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(usuarioRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> authController.fazerLogin(loginRequest)
        );
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando cliente não encontrado após autenticação")
    void deveLancarRecursoNaoEncontradoExceptionQuandoClienteNaoEncontrado() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(usuarioRepository.findByEmail(any(String.class))).thenReturn(Optional.of(usuario));
        when(clienteRepository.findByUsuarioId(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> authController.fazerLogin(loginRequest)
        );
    }

    @Test
    @DisplayName("Token JWT deve ser gerado com ID e email corretos")
    void tokenJwtDeveSerGeradoComIdEEmailCorretos() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(usuarioRepository.findByEmail(any(String.class))).thenReturn(Optional.of(usuario));
        when(clienteRepository.findByUsuarioId(any(UUID.class))).thenReturn(Optional.of(cliente));
        when(jwtUtil.generateToken(any(UUID.class), any(String.class))).thenReturn("jwt-token-gerado");

        authController.fazerLogin(loginRequest);

        verify(jwtUtil).generateToken(usuario.getId(), usuario.getEmail());
    }
}