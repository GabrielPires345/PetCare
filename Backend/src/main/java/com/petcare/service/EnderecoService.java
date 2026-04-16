package com.petcare.service;

import com.petcare.mapper.EnderecoMapper;
import com.petcare.mapper.request.EnderecoRequest;
import com.petcare.mapper.response.EnderecoResponse;
import com.petcare.model.Cliente;
import com.petcare.model.Clinica;
import com.petcare.model.Endereco;
import com.petcare.repository.ClienteRepository;
import com.petcare.repository.ClinicaRepository;
import com.petcare.repository.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository clienteRepository;
    private final ClinicaRepository clinicaRepository;

    public EnderecoResponse criarEnderecoParaCliente(UUID clienteId, EnderecoRequest enderecoRequest) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente not found"));

        Endereco endereco = EnderecoMapper.toEndereco(enderecoRequest);
        endereco.setCliente(cliente);
        endereco.setClinica(null); // Garantir que não esteja associado a uma clinica

        Endereco savedEndereco = enderecoRepository.save(endereco);
        return EnderecoMapper.toEnderecoResponse(savedEndereco);
    }

    public EnderecoResponse criarEnderecoParaClinica(UUID clinicaId, EnderecoRequest enderecoRequest) {
        Clinica clinica = clinicaRepository.findById(clinicaId)
                .orElseThrow(() -> new RuntimeException("Clinica not found"));

        Endereco endereco = EnderecoMapper.toEndereco(enderecoRequest);
        endereco.setClinica(clinica);
        endereco.setCliente(null); // Garantir que não esteja associado a um cliente

        Endereco savedEndereco = enderecoRepository.save(endereco);
        return EnderecoMapper.toEnderecoResponse(savedEndereco);
    }

    public EnderecoResponse getEnderecoById(UUID id) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereco not found"));
        return EnderecoMapper.toEnderecoResponse(endereco);
    }

    public List<EnderecoResponse> getAllEnderecos() {
        return enderecoRepository.findAll().stream()
                .map(EnderecoMapper::toEnderecoResponse)
                .collect(Collectors.toList());
    }

    public List<EnderecoResponse> getEnderecosByClienteId(UUID clienteId) {
        return enderecoRepository.findByCliente_Id(clienteId).stream()
                .map(EnderecoMapper::toEnderecoResponse)
                .collect(Collectors.toList());
    }

    public List<EnderecoResponse> getEnderecosByClinicaId(UUID clinicaId) {
        return enderecoRepository.findByClinica_Id(clinicaId).stream()
                .map(EnderecoMapper::toEnderecoResponse)
                .collect(Collectors.toList());
    }

    public EnderecoResponse atualizarEndereco(UUID id, EnderecoRequest enderecoRequest) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereco not found"));

        endereco.setBairro(enderecoRequest.bairro());
        endereco.setNumero(enderecoRequest.numero());
        endereco.setLogradouro(enderecoRequest.logradouro());
        endereco.setCidade(enderecoRequest.cidade());
        endereco.setUf(enderecoRequest.uf());
        endereco.setCep(enderecoRequest.cep());

        Endereco updatedEndereco = enderecoRepository.save(endereco);
        return EnderecoMapper.toEnderecoResponse(updatedEndereco);
    }

    public void deletarEndereco(UUID id) {
        if (!enderecoRepository.existsById(id)) {
            throw new RuntimeException("Endereco not found");
        }
        enderecoRepository.deleteById(id);
    }
}