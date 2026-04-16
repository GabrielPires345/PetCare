package com.petcare.controller;

import com.petcare.mapper.request.ClinicaRequest;
import com.petcare.mapper.request.EnderecoRequest;
import com.petcare.mapper.request.TelefoneRequest;
import com.petcare.mapper.response.ClinicaResponse;
import com.petcare.mapper.response.EnderecoResponse;
import com.petcare.mapper.response.TelefoneResponse;
import com.petcare.model.Clinica;
import com.petcare.service.ClinicaService;
import com.petcare.service.EnderecoService;
import com.petcare.service.TelefoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clinicas")
@RequiredArgsConstructor
public class ClinicaController {

    private final ClinicaService clinicaService;
    private final EnderecoService enderecoService;
    private final TelefoneService telefoneService;

    @GetMapping
    public ResponseEntity<List<ClinicaResponse>> getAllClinicas() {
        List<ClinicaResponse> clinicas = clinicaService.getAllClinicas();
        return ResponseEntity.ok(clinicas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicaResponse> getClinicaById(@PathVariable UUID id) {
        ClinicaResponse clinica = clinicaService.getClinicaById(id);
        return ResponseEntity.ok(clinica);
    }

    @PostMapping
    public ResponseEntity<ClinicaResponse> createClinica(@Valid @RequestBody ClinicaRequest clinicaRequest) {
        ClinicaResponse clinica = clinicaService.createClinica(clinicaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(clinica);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClinicaResponse> updateClinica(@PathVariable UUID id, @Valid @RequestBody ClinicaRequest clinicaRequest) {
        ClinicaResponse clinica = clinicaService.updateClinica(id, clinicaRequest);
        return ResponseEntity.ok(clinica);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClinica(@PathVariable UUID id) {
        clinicaService.deleteClinica(id);
        return ResponseEntity.noContent().build();
    }

    // Endereços da clinica
    @PostMapping("/{id}/enderecos")
    public ResponseEntity<EnderecoResponse> adicionarEndereco(@PathVariable UUID id, @Valid @RequestBody EnderecoRequest enderecoRequest) {
        EnderecoResponse endereco = enderecoService.criarEnderecoParaClinica(id, enderecoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(endereco);
    }

    @GetMapping("/{id}/enderecos")
    public ResponseEntity<List<EnderecoResponse>> listarEnderecos(@PathVariable UUID id) {
        List<EnderecoResponse> enderecos = enderecoService.getEnderecosByClinicaId(id);
        return ResponseEntity.ok(enderecos);
    }

    // Telefones da clinica
    @PostMapping("/{id}/telefones")
    public ResponseEntity<TelefoneResponse> adicionarTelefone(@PathVariable UUID id, @RequestBody TelefoneRequest telefoneRequest) {
        TelefoneResponse telefone = telefoneService.criarTelefoneParaClinica(id, telefoneRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(telefone);
    }

    @GetMapping("/{id}/telefones")
    public ResponseEntity<List<TelefoneResponse>> listarTelefones(@PathVariable UUID id) {
        List<TelefoneResponse> telefones = telefoneService.getTelefonesByClinicaId(id);
        return ResponseEntity.ok(telefones);
    }
}