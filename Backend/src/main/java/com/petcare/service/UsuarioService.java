package com.petcare.service;

import com.petcare.mapper.UserMapper;
import com.petcare.mapper.request.UserRequest;
import com.petcare.mapper.response.UserResponse;
import com.petcare.model.Usuario;
import com.petcare.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUsuario(UserRequest userRequest) {
        if (usuarioRepository.findByEmail(userRequest.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Usuario usuario = UserMapper.toUser(userRequest);
        usuario.setPasswordHash(passwordEncoder.encode(userRequest.password()));
        Usuario savedUsuario = usuarioRepository.save(usuario);
        return UserMapper.toUserResponse(savedUsuario);
    }

    public UserResponse getUsuarioById(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario not found"));
        return UserMapper.toUserResponse(usuario);
    }

    public List<UserResponse> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(UserMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse updateUsuario(UUID id, UserRequest userRequest) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario not found"));

        usuario.setEmail(userRequest.email());
        usuario.setPasswordHash(passwordEncoder.encode(userRequest.password()));
        usuario.setNivelAcesso(userRequest.nivelAcesso());

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return UserMapper.toUserResponse(updatedUsuario);
    }

    public void deleteUsuario(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario not found");
        }
        usuarioRepository.deleteById(id);
    }
}