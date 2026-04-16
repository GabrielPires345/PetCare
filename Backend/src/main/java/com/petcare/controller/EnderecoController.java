package com.petcare.controller;

import com.petcare.mapper.request.EnderecoRequest;
import com.petcare.mapper.response.EnderecoResponse;
import com.petcare.service.EnderecoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/enderecos")
@RequiredArgsConstructor
public class EnderecoController {

    private final EnderecoService enderecoService;

    // Endpoints gerais (para administração, auditoria, etc.)
    @GetMapping
    public ResponseEntity<List<EnderecoResponse>> getAllEnderecos() {
        List<EnderecoResponse> enderecos = enderecoService.getAllEnderecos();
        return ResponseEntity.ok(enderecos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponse> getEnderecoById(@PathVariable UUID id) {
        EnderecoResponse endereco = enderecoService.getEnderecoById(id);
        return ResponseEntity.ok(endereco);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnderecoResponse> atualizarEndereco(@PathVariable UUID id, @Valid @RequestBody EnderecoRequest enderecoRequest) {
        EnderecoResponse endereco = enderecoService.atualizarEndereco(id, enderecoRequest);
        return ResponseEntity.ok(endereco);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEndereco(@PathVariable UUID id) {
        enderecoService.deletarEndereco(id);
        return ResponseEntity.noContent().build();
    }
}