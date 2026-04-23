package com.petcare.service;

import com.petcare.exception.RecursoNaoEncontradoException;
import com.petcare.mapper.VeterinarioMapper;
import com.petcare.mapper.request.VeterinarioCreateRequest;
import com.petcare.mapper.request.VeterinarioRequest;
import com.petcare.mapper.response.VeterinarioResponse;
import com.petcare.model.Especialidade;
import com.petcare.model.Usuario;
import com.petcare.model.Veterinario;
import com.petcare.repository.EspecialidadeRepository;
import com.petcare.repository.UsuarioRepository;
import com.petcare.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<VeterinarioResponse> getAllVeterinarios() {
        return veterinarioRepository.findAll().stream()
                .map(VeterinarioMapper::toVeterinarioResponse)
                .toList();
    }

    public VeterinarioResponse getVeterinarioById(UUID id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado"));
        return VeterinarioMapper.toVeterinarioResponse(veterinario);
    }

    @Transactional
    public VeterinarioResponse createVeterinario(VeterinarioRequest request) {
        Usuario usuario = Usuario.builder()
                .email(request.crmv() + "@vet.petcare")
                .nomeUsuario(request.nome().length() <= 10 ? request.nome() : request.nome().substring(0, 10))
                .senhaHash(passwordEncoder.encode(request.crmv()))
                .nivelAcesso("VETERINARIO")
                .build();
        usuario = usuarioRepository.save(usuario);

        Veterinario veterinario = VeterinarioMapper.toVeterinario(request, especialidadeRepository);
        veterinario.setUsuario(usuario);

        Veterinario saved = veterinarioRepository.save(veterinario);
        return VeterinarioMapper.toVeterinarioResponse(saved);
    }

    @Transactional
    public VeterinarioResponse registrarNovoVeterinario(VeterinarioCreateRequest dto, Usuario usuario) {
        Veterinario veterinario = Veterinario.builder()
                .usuario(usuario)
                .nome(dto.nome())
                .crmv(dto.crmv())
                .build();

        if (dto.especialidadeIds() != null && !dto.especialidadeIds().isEmpty()) {
            Set<Especialidade> especialidades = dto.especialidadeIds().stream()
                    .map(id -> especialidadeRepository.findById(id)
                            .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade não encontrada: " + id)))
                    .collect(Collectors.toSet());
            veterinario.setEspecialidades(especialidades);
        }

        Veterinario saved = veterinarioRepository.save(veterinario);
        return VeterinarioMapper.toVeterinarioResponse(saved);
    }

    @Transactional
    public VeterinarioResponse updateVeterinario(UUID id, VeterinarioRequest request) {
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado"));

        veterinario.setNome(request.nome());
        veterinario.setCrmv(request.crmv());

        if (request.especialidadeIds() != null) {
            Set<Especialidade> especialidades = request.especialidadeIds().stream()
                    .map(especialidadeId -> especialidadeRepository.findById(especialidadeId)
                            .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade não encontrada: " + especialidadeId)))
                    .collect(Collectors.toSet());
            veterinario.setEspecialidades(especialidades);
        }

        return VeterinarioMapper.toVeterinarioResponse(veterinarioRepository.save(veterinario));
    }

    public void deleteVeterinario(UUID id) {
        if (!veterinarioRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Veterinário não encontrado");
        }
        veterinarioRepository.deleteById(id);
    }
}
