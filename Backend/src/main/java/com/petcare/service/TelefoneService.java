package com.petcare.service;

import com.petcare.mapper.TelefoneMapper;
import com.petcare.mapper.request.TelefoneRequest;
import com.petcare.mapper.response.TelefoneResponse;
import com.petcare.model.Cliente;
import com.petcare.model.Clinica;
import com.petcare.model.Telefone;
import com.petcare.repository.ClienteRepository;
import com.petcare.repository.ClinicaRepository;
import com.petcare.repository.TelefoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelefoneService {

    private final TelefoneRepository telefoneRepository;
    private final ClienteRepository clienteRepository;
    private final ClinicaRepository clinicaRepository;

    public TelefoneResponse criarTelefoneParaCliente(UUID clienteId, TelefoneRequest telefoneRequest) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente not found"));

        Telefone telefone = TelefoneMapper.toTelefone(telefoneRequest);
        telefone.setCliente(cliente);
        telefone.setClinica(null); // Garantir que não esteja associado a uma clinica

        Telefone savedTelefone = telefoneRepository.save(telefone);
        return TelefoneMapper.toTelefoneResponse(savedTelefone);
    }

    public TelefoneResponse criarTelefoneParaClinica(UUID clinicaId, TelefoneRequest telefoneRequest) {
        Clinica clinica = clinicaRepository.findById(clinicaId)
                .orElseThrow(() -> new RuntimeException("Clinica not found"));

        Telefone telefone = TelefoneMapper.toTelefone(telefoneRequest);
        telefone.setClinica(clinica);
        telefone.setCliente(null); // Garantir que não esteja associado a um cliente

        Telefone savedTelefone = telefoneRepository.save(telefone);
        return TelefoneMapper.toTelefoneResponse(savedTelefone);
    }

    public TelefoneResponse getTelefoneById(UUID id) {
        Telefone telefone = telefoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Telefone not found"));
        return TelefoneMapper.toTelefoneResponse(telefone);
    }

    public List<TelefoneResponse> getAllTelefones() {
        return telefoneRepository.findAll().stream()
                .map(TelefoneMapper::toTelefoneResponse)
                .collect(Collectors.toList());
    }

    public List<TelefoneResponse> getTelefonesByClienteId(UUID clienteId) {
        return telefoneRepository.findByCliente_Id(clienteId).stream()
                .map(TelefoneMapper::toTelefoneResponse)
                .collect(Collectors.toList());
    }

    public List<TelefoneResponse> getTelefonesByClinicaId(UUID clinicaId) {
        return telefoneRepository.findByClinica_Id(clinicaId).stream()
                .map(TelefoneMapper::toTelefoneResponse)
                .collect(Collectors.toList());
    }

    public TelefoneResponse atualizarTelefone(UUID id, TelefoneRequest telefoneRequest) {
        Telefone telefone = telefoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Telefone not found"));

        telefone.setDdd(telefoneRequest.ddd());
        telefone.setNumero(telefoneRequest.numero());
        telefone.setWhatsapp(telefoneRequest.whatsapp());

        Telefone updatedTelefone = telefoneRepository.save(telefone);
        return TelefoneMapper.toTelefoneResponse(updatedTelefone);
    }

    public void deletarTelefone(UUID id) {
        if (!telefoneRepository.existsById(id)) {
            throw new RuntimeException("Telefone not found");
        }
        telefoneRepository.deleteById(id);
    }
}