package com.petcare.service;

import com.petcare.exception.RecursoNaoEncontradoException;
import com.petcare.mapper.PetMapper;
import com.petcare.mapper.request.PetRequest;
import com.petcare.mapper.response.PetResponse;
import com.petcare.model.Cliente;
import com.petcare.model.Pet;
import com.petcare.repository.ClienteRepository;
import com.petcare.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final ClienteRepository clienteRepository;

    public PetResponse adicionarPet(UUID clienteId, PetRequest petRequest) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));

        Pet pet = PetMapper.toPet(petRequest);
        pet.setCliente(cliente);

        Pet savedPet = petRepository.save(pet);
        return PetMapper.toPetResponse(savedPet);
    }

    public PetResponse getPetById(UUID petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado"));
        return PetMapper.toPetResponse(pet);
    }

    public void deletePet(UUID petId) {
        if (!petRepository.existsById(petId)) {
            throw new RecursoNaoEncontradoException("Pet não encontrado");
        }
        petRepository.deleteById(petId);
    }
}
