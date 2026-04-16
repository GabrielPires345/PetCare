package com.petcare.service;

import com.petcare.exception.RecursoDuplicadoException;
import com.petcare.exception.RecursoNaoEncontradoException;
import com.petcare.mapper.UserMapper;
import com.petcare.mapper.request.ClienteCreateRequest;
import com.petcare.mapper.request.UserRequest;
import com.petcare.mapper.response.UserResponse;
import com.petcare.model.Usuario;
import com.petcare.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean existsByEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public Usuario criarUsuarioParaNovoCliente(ClienteCreateRequest dto) {
        // Verifica se o e-mail já está cadastrado
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RecursoDuplicadoException("Email já cadastrado");
        }

        Usuario usuario = Usuario.builder()
                .nomeUsuario(dto.nomeUsuario())
                .email(dto.email())
                .senhaHash(passwordEncoder.encode(dto.senha()))
                .nivelAcesso("CLIENTE")
                .build();

        return usuarioRepository.save(usuario);
    }

    public UserResponse getUsuarioById(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        return UserMapper.toUserResponse(usuario);
    }

    public List<UserResponse> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(UserMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUsuario(UUID id, UserRequest userRequest) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        usuario.setEmail(userRequest.email());
        usuario.setNivelAcesso(userRequest.nivelAcesso());

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return UserMapper.toUserResponse(updatedUsuario);
    }

    public void deleteUsuario(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}
