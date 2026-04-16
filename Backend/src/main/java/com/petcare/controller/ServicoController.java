package com.petcare.controller;

import com.petcare.mapper.request.ServicoRequest;
import com.petcare.mapper.response.ServicoResponse;
import com.petcare.service.ServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService servicoService;

    @GetMapping
    public ResponseEntity<List<ServicoResponse>> getAllServicos() {
        return ResponseEntity.ok(servicoService.getAllServicos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponse> getServicoById(@PathVariable UUID id) {
        return ResponseEntity.ok(servicoService.getServicoById(id));
    }

    @PostMapping
    public ResponseEntity<ServicoResponse> createServico(@Valid @RequestBody ServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoService.createServico(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponse> updateServico(@PathVariable UUID id, @Valid @RequestBody ServicoRequest request) {
        return ResponseEntity.ok(servicoService.updateServico(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServico(@PathVariable UUID id) {
        servicoService.deleteServico(id);
        return ResponseEntity.noContent().build();
    }
}
