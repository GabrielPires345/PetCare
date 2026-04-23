package com.petcare.controller;

import com.petcare.exception.AutenticacaoException;
import com.petcare.exception.ErrorCode;
import com.petcare.exception.RecursoNaoEncontradoException;
import com.petcare.mapper.UserMapper;
import com.petcare.mapper.request.ClienteCreateRequest;
import com.petcare.mapper.request.ClinicaCreateRequest;
import com.petcare.mapper.request.LoginRequest;
import com.petcare.mapper.request.VeterinarioCreateRequest;
import com.petcare.mapper.response.UserResponse;
import com.petcare.model.Clinica;
import com.petcare.model.Cliente;
import com.petcare.model.Usuario;
import com.petcare.model.Veterinario;
import com.petcare.repository.ClinicaRepository;
import com.petcare.repository.ClienteRepository;
import com.petcare.repository.UsuarioRepository;
import com.petcare.repository.VeterinarioRepository;
import com.petcare.security.JwtUtil;
import com.petcare.service.ClinicaService;
import com.petcare.service.ClienteService;
import com.petcare.service.UsuarioService;
import com.petcare.service.VeterinarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ClinicaRepository clinicaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final ClinicaService clinicaService;
    private final VeterinarioService veterinarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registrarCliente(@Valid @RequestBody ClienteCreateRequest clienteCreateRequest) {
        if (!clienteCreateRequest.senha().equals(clienteCreateRequest.confirmaSenha())) {
            throw new AutenticacaoException(ErrorCode.SENHAS_NAO_CONFEREM, "As senhas não conferem");
        }

        Usuario savedUsuario = usuarioService.criarUsuarioParaNovoCliente(clienteCreateRequest);
        clienteService.registrarNovoCliente(clienteCreateRequest, savedUsuario);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("mensagem", "Registro realizado com sucesso. Verifique seu email para ativar sua conta.");
        responseBody.put("email", savedUsuario.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PostMapping("/registro-clinica")
    public ResponseEntity<Map<String, Object>> registrarClinica(@Valid @RequestBody ClinicaCreateRequest clinicaCreateRequest) {
        if (!clinicaCreateRequest.senha().equals(clinicaCreateRequest.confirmaSenha())) {
            throw new AutenticacaoException(ErrorCode.SENHAS_NAO_CONFEREM, "As senhas não conferem");
        }

        Usuario savedUsuario = usuarioService.criarUsuarioParaNovaClinica(clinicaCreateRequest);
        clinicaService.registrarNovaClinica(clinicaCreateRequest, savedUsuario);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("mensagem", "Registro realizado com sucesso. Verifique seu email para ativar sua conta.");
        responseBody.put("email", savedUsuario.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PostMapping("/registro-veterinario")
    public ResponseEntity<Map<String, Object>> registrarVeterinario(@Valid @RequestBody VeterinarioCreateRequest veterinarioCreateRequest) {
        if (!veterinarioCreateRequest.senha().equals(veterinarioCreateRequest.confirmaSenha())) {
            throw new AutenticacaoException(ErrorCode.SENHAS_NAO_CONFEREM, "As senhas não conferem");
        }

        Usuario savedUsuario = usuarioService.criarUsuarioParaNovoVeterinario(veterinarioCreateRequest);
        veterinarioService.registrarNovoVeterinario(veterinarioCreateRequest, savedUsuario);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("mensagem", "Registro realizado com sucesso. Verifique seu email para ativar sua conta.");
        responseBody.put("email", savedUsuario.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> fazerLogin(@Valid @RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.senha()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (!Boolean.TRUE.equals(usuario.getEmailVerificado())) {
            throw new AutenticacaoException(ErrorCode.EMAIL_NAO_VERIFICADO, "Email ainda não verificado. Verifique sua caixa de entrada.");
        }

        UUID perfilId = buscarPerfilId(usuario);
        String tipoPerfil = usuario.getNivelAcesso();

        String token = jwtUtil.generateToken(perfilId, usuario.getEmail());
        UserResponse response = UserMapper.toUserResponse(usuario);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("user", response);
        responseBody.put("perfilId", perfilId);
        responseBody.put("tipoPerfil", tipoPerfil);
        responseBody.put("token", token);

        return ResponseEntity.ok(responseBody);
    }

    private UUID buscarPerfilId(Usuario usuario) {
        return switch (usuario.getNivelAcesso()) {
            case "CLIENTE" -> clienteRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil de cliente não encontrado"))
                    .getId();
            case "CLINICA" -> clinicaRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil de clínica não encontrado"))
                    .getId();
            case "VETERINARIO" -> veterinarioRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil de veterinário não encontrado"))
                    .getId();
            default -> throw new AutenticacaoException(ErrorCode.CREDENCIAIS_INVALIDAS, "Tipo de perfil desconhecido");
        };
    }

    @GetMapping("/verificar-email")
    public ResponseEntity<Map<String, String>> verificarEmail(@RequestParam String token) {
        usuarioService.verificarEmail(token);

        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Email verificado com sucesso! Você já pode fazer login.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reenviar-verificacao")
    public ResponseEntity<Map<String, String>> reenviarVerificacao(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            throw new AutenticacaoException(ErrorCode.PARAMETRO_FALTANDO, "Email é obrigatório");
        }

        usuarioService.reenviarVerificacao(email);

        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Email de verificação reenviado com sucesso.");

        return ResponseEntity.ok(response);
    }
}
