package com.petcare.controller;

import com.petcare.mapper.request.VeterinarioRequest;
import com.petcare.mapper.response.VeterinarioResponse;
import com.petcare.service.VeterinarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/veterinarios")
@RequiredArgsConstructor
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    @GetMapping
    public ResponseEntity<List<VeterinarioResponse>> getAllVeterinarios() {
        return ResponseEntity.ok(veterinarioService.getAllVeterinarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioResponse> getVeterinarioById(@PathVariable UUID id) {
        return ResponseEntity.ok(veterinarioService.getVeterinarioById(id));
    }

    @PostMapping
    public ResponseEntity<VeterinarioResponse> createVeterinario(@Valid @RequestBody VeterinarioRequest veterinarioRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(veterinarioService.createVeterinario(veterinarioRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioResponse> updateVeterinario(
            @PathVariable UUID id,
            @Valid @RequestBody VeterinarioRequest veterinarioRequest) {
        return ResponseEntity.ok(veterinarioService.updateVeterinario(id, veterinarioRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeterinario(@PathVariable UUID id) {
        veterinarioService.deleteVeterinario(id);
        return ResponseEntity.noContent().build();
    }
}