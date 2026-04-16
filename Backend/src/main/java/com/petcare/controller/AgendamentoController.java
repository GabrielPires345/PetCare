package com.petcare.controller;

import com.petcare.mapper.request.AgendamentoRequest;
import com.petcare.mapper.response.AgendamentoResponse;
import com.petcare.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity<AgendamentoResponse> agendar(@Valid @RequestBody AgendamentoRequest request) {
        AgendamentoResponse response = agendamentoService.agendar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponse> getById(@PathVariable UUID id) {
        AgendamentoResponse response = agendamentoService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<AgendamentoResponse>> getByClienteId(@PathVariable UUID clienteId) {
        List<AgendamentoResponse> responses = agendamentoService.getByClienteId(clienteId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable UUID id) {
        agendamentoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}