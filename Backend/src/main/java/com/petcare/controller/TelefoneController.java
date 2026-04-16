package com.petcare.controller;

import com.petcare.mapper.request.TelefoneRequest;
import com.petcare.mapper.response.TelefoneResponse;
import com.petcare.service.TelefoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/telefones")
@RequiredArgsConstructor
public class TelefoneController {

    private final TelefoneService telefoneService;

    // Endpoints gerais (para administração, auditoria, etc.)
    @GetMapping
    public ResponseEntity<List<TelefoneResponse>> getAllTelefones() {
        List<TelefoneResponse> telefones = telefoneService.getAllTelefones();
        return ResponseEntity.ok(telefones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TelefoneResponse> getTelefoneById(@PathVariable UUID id) {
        TelefoneResponse telefone = telefoneService.getTelefoneById(id);
        return ResponseEntity.ok(telefone);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TelefoneResponse> atualizarTelefone(@PathVariable UUID id, @RequestBody TelefoneRequest telefoneRequest) {
        TelefoneResponse telefone = telefoneService.atualizarTelefone(id, telefoneRequest);
        return ResponseEntity.ok(telefone);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTelefone(@PathVariable UUID id) {
        telefoneService.deletarTelefone(id);
        return ResponseEntity.noContent().build();
    }
}