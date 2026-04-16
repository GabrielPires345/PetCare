package com.petcare.service;

import com.petcare.mapper.PetMapper;
import com.petcare.mapper.request.PetRequest;
import com.petcare.mapper.response.PetResponse;
import com.petcare.model.Cliente;
import com.petcare.model.Pet;
import com.petcare.repository.ClienteRepository;
import com.petcare.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetServiceTest {

    @Mock private PetRepository petRepository;
    @Mock private ClienteRepository clienteRepository;

    @InjectMocks
    private PetService petService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddPetToCliente() {
        UUID clienteId = UUID.randomUUID();
        Cliente cliente = Cliente.builder().id(clienteId).build();
        PetRequest request = new PetRequest("Rex", "Cachorro", "Macho", 5.0, LocalDate.of(2023, 1, 1), false);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> {
            Pet p = inv.getArgument(0);
            return Pet.builder().id(UUID.randomUUID()).nome(p.getNome()).especie(p.getEspecie()).sexo(p.getSexo()).peso(p.getPeso()).dataNascimento(p.getDataNascimento()).castrado(p.getCastrado()).build();
        });

        PetResponse result = petService.adicionarPet(clienteId, request);

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("Rex", result.nome());
        assertEquals("Cachorro", result.especie());
        assertEquals(5.0, result.peso());
    }

    @Test
    void shouldThrowWhenClienteNotFoundForPet() {
        UUID clienteId = UUID.randomUUID();
        PetRequest request = new PetRequest("Rex", "Cachorro", "Macho", 5.0, LocalDate.of(2023, 1, 1), false);
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> petService.adicionarPet(clienteId, request));
    }

    @Test
    void shouldReturnPetById() {
        UUID id = UUID.randomUUID();
        Pet pet = Pet.builder().id(id).nome("Rex").especie("Cachorro").sexo("Macho").peso(5.0).dataNascimento(LocalDate.of(2023, 1, 1)).castrado(false).build();
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));

        PetResponse result = petService.getPetById(id);

        assertEquals("Rex", result.nome());
        assertEquals("Cachorro", result.especie());
    }

    @Test
    void shouldThrowWhenPetNotFound() {
        when(petRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> petService.getPetById(UUID.randomUUID()));
    }

    @Test
    void shouldDeletePet() {
        UUID id = UUID.randomUUID();
        when(petRepository.existsById(id)).thenReturn(true);

        petService.deletePet(id);

        verify(petRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentPet() {
        when(petRepository.existsById(any())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> petService.deletePet(UUID.randomUUID()));
    }
}
