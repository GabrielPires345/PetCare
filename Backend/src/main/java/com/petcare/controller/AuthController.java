package com.petcare.controller;

import com.petcare.mapper.UserMapper;
import com.petcare.mapper.request.ClienteCreateRequest;
import com.petcare.mapper.request.UserRequest;
import com.petcare.mapper.response.UserResponse;
import com.petcare.model.Cliente;
import com.petcare.model.Usuario;
import com.petcare.repository.ClienteRepository;
import com.petcare.repository.UsuarioRepository;
import com.petcare.security.JwtUtil;
import com.petcare.service.ClienteService;
import com.petcare.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/registro")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody ClienteCreateRequest clienteCreateRequest) {
        if (!clienteCreateRequest.senha().equals(clienteCreateRequest.confirmaSenha())) {
            return ResponseEntity.badRequest().body("As senhas não conferem");
        }
        Usuario savedUsuario = usuarioService.criarUsuarioParaNovoCliente(clienteCreateRequest);

        clienteService.registrarNovoCliente(clienteCreateRequest, savedUsuario);

        Cliente cliente = clienteRepository.findByUsuarioId(savedUsuario.getId())
                .orElseThrow(() -> new RuntimeException("Cliente not found after registration"));

        String token = jwtUtil.generateToken(savedUsuario.getId(), savedUsuario.getEmail());
        UserResponse userResponse = UserMapper.toUserResponse(savedUsuario);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("user", userResponse);
        responseBody.put("token", token);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PostMapping("/login")
    public ResponseEntity<?> fazerLogin(@RequestBody UserRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.senha()
                    )
            );

            Usuario usuario = usuarioRepository.findByEmail(loginRequest.email())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new RuntimeException("Cliente profile not found"));

            String token = jwtUtil.generateToken(usuario.getId(), usuario.getEmail());
            UserResponse response = UserMapper.toUserResponse(usuario);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("user", response);
            responseBody.put("clienteId", cliente.getId());
            responseBody.put("token", token);

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
