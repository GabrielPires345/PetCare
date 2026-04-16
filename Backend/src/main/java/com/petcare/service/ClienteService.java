package com.petcare.service;

import com.petcare.exception.RecursoDuplicadoException;
import com.petcare.exception.RecursoNaoEncontradoException;
import com.petcare.mapper.ClienteMapper;
import com.petcare.mapper.PetMapper;
import com.petcare.mapper.request.ClienteCreateRequest;
import com.petcare.mapper.request.ClienteUpdateRequest;
import com.petcare.mapper.request.PetRequest;
import com.petcare.mapper.response.ClienteResponse;
import com.petcare.model.Cliente;
import com.petcare.model.Pet;
import com.petcare.model.Usuario;
import com.petcare.repository.ClienteRepository;
import com.petcare.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PetRepository petRepository;
    private final UsuarioService usuarioService;

    @Transactional
    public ClienteResponse registrarNovoCliente(ClienteCreateRequest dto, Usuario usuario) {
        // 1. Converte e vincula o usuário obrigatoriamente
        Cliente cliente = ClienteMapper.toCliente(dto);
        cliente.setUsuario(usuario);
        Cliente clienteSalvo = clienteRepository.save(cliente);

        // 2. Salva o Pet (obrigatório no cadastro)
        Pet pet = PetMapper.toPet(dto.pet());
        pet.setCliente(clienteSalvo);
        petRepository.save(pet);

        return ClienteMapper.toResponse(clienteSalvo);
    }

    public ClienteResponse getClienteById(UUID id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
        return ClienteMapper.toResponse(cliente);
    }

    public List<ClienteResponse> getAllClientes() {
        return clienteRepository.findAll().stream()
                .map(ClienteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClienteResponse updateCliente(UUID id, ClienteUpdateRequest clienteUpdateRequest) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));

        // Verificar se o CPF já existe em outro cliente
        clienteRepository.findByCpf(clienteUpdateRequest.cpf())
                .ifPresent(existingCliente -> {
                    if (!existingCliente.getId().equals(id)) {
                        throw new RecursoDuplicadoException("CPF já cadastrado para outro cliente");
                    }
                });

        ClienteMapper.updateClienteFromRequest(clienteUpdateRequest, cliente);
        Cliente updatedCliente = clienteRepository.save(cliente);
        return ClienteMapper.toResponse(updatedCliente);
    }

    public void deleteCliente(UUID id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado");
        }
        clienteRepository.deleteById(id);
    }
}
