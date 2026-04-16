package com.petcare.service;

import com.petcare.mapper.EspecialidadeMapper;
import com.petcare.mapper.request.EspecialidadeRequest;
import com.petcare.mapper.response.EspecialidadeResponse;
import com.petcare.model.Especialidade;
import com.petcare.repository.EspecialidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspecialidadeService {

    private final EspecialidadeRepository especialidadeRepository;

    public EspecialidadeResponse criarEspecialidade(EspecialidadeRequest especialidadeRequest) {
        Especialidade especialidade = EspecialidadeMapper.toEspecialidade(especialidadeRequest);
        Especialidade savedEspecialidade = especialidadeRepository.save(especialidade);
        return EspecialidadeMapper.toEspecialidadeResponse(savedEspecialidade);
    }

    public EspecialidadeResponse getEspecialidadeById(UUID id) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidade not found"));
        return EspecialidadeMapper.toEspecialidadeResponse(especialidade);
    }

    public List<EspecialidadeResponse> getAllEspecialidades() {
        return especialidadeRepository.findAll().stream()
                .map(EspecialidadeMapper::toEspecialidadeResponse)
                .collect(Collectors.toList());
    }

    public EspecialidadeResponse atualizarEspecialidade(UUID id, EspecialidadeRequest especialidadeRequest) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidade not found"));

        especialidade.setNome(especialidadeRequest.nome());
        Especialidade updatedEspecialidade = especialidadeRepository.save(especialidade);
        return EspecialidadeMapper.toEspecialidadeResponse(updatedEspecialidade);
    }

    public void deletarEspecialidade(UUID id) {
        if (!especialidadeRepository.existsById(id)) {
            throw new RuntimeException("Especialidade not found");
        }
        especialidadeRepository.deleteById(id);
    }
}