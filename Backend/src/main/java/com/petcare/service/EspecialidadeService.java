package com.petcare.service;

import com.petcare.exception.RecursoNaoEncontradoException;
import com.petcare.mapper.EspecialidadeMapper;
import com.petcare.mapper.request.EspecialidadeRequest;
import com.petcare.mapper.response.EspecialidadeResponse;
import com.petcare.model.Especialidade;
import com.petcare.repository.EspecialidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspecialidadeService {

    private final EspecialidadeRepository especialidadeRepository;

    @Transactional
    public EspecialidadeResponse criarEspecialidade(EspecialidadeRequest especialidadeRequest) {
        Especialidade especialidade = EspecialidadeMapper.toEspecialidade(especialidadeRequest);
        Especialidade savedEspecialidade = especialidadeRepository.save(especialidade);
        return EspecialidadeMapper.toEspecialidadeResponse(savedEspecialidade);
    }

    public EspecialidadeResponse getEspecialidadeById(UUID id) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade não encontrada"));
        return EspecialidadeMapper.toEspecialidadeResponse(especialidade);
    }

    public List<EspecialidadeResponse> getAllEspecialidades() {
        return especialidadeRepository.findAll().stream()
                .map(EspecialidadeMapper::toEspecialidadeResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EspecialidadeResponse atualizarEspecialidade(UUID id, EspecialidadeRequest especialidadeRequest) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade não encontrada"));

        especialidade.setNome(especialidadeRequest.nome());
        Especialidade updatedEspecialidade = especialidadeRepository.save(especialidade);
        return EspecialidadeMapper.toEspecialidadeResponse(updatedEspecialidade);
    }

    @Transactional
    public void deletarEspecialidade(UUID id) {
        if (!especialidadeRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Especialidade não encontrada");
        }
        especialidadeRepository.deleteById(id);
    }
}
