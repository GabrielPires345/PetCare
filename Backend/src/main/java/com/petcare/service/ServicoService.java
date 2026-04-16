package com.petcare.service;

import com.petcare.exception.RecursoNaoEncontradoException;
import com.petcare.mapper.ServicoMapper;
import com.petcare.mapper.request.ServicoRequest;
import com.petcare.mapper.response.ServicoResponse;
import com.petcare.model.Servico;
import com.petcare.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;

    @Transactional
    public ServicoResponse createServico(ServicoRequest request) {
        Servico servico = ServicoMapper.toServico(request);
        Servico saved = servicoRepository.save(servico);
        return ServicoMapper.toServicoResponse(saved);
    }

    public List<ServicoResponse> getAllServicos() {
        return servicoRepository.findAll().stream()
                .map(ServicoMapper::toServicoResponse)
                .collect(Collectors.toList());
    }

    public ServicoResponse getServicoById(UUID id) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado"));
        return ServicoMapper.toServicoResponse(servico);
    }

    @Transactional
    public ServicoResponse updateServico(UUID id, ServicoRequest request) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado"));

        servico.setNome(request.nome());
        servico.setDescricao(request.descricao());
        servico.setPrecoBase(request.precoBase());
        servico.setDuracaoMinutos(request.duracaoMinutos());

        return ServicoMapper.toServicoResponse(servicoRepository.save(servico));
    }

    public void deleteServico(UUID id) {
        if (!servicoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Serviço não encontrado");
        }
        servicoRepository.deleteById(id);
    }
}
