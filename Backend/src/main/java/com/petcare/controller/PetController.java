package com.petcare.controller;

import com.petcare.mapper.request.PetRequest;
import com.petcare.mapper.response.PetResponse;
import com.petcare.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<PetResponse> adicionarPet(
            @PathVariable UUID clienteId,
            @Valid @RequestBody PetRequest petRequest) {
        PetResponse pet = petService.adicionarPet(clienteId, petRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(pet);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> getPetById(@PathVariable UUID id) {
        PetResponse pet = petService.getPetById(id);
        return ResponseEntity.ok(pet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable UUID id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}