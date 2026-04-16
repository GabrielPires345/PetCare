package com.petcare.controller;

import com.petcare.mapper.request.EspecialidadeRequest;
import com.petcare.mapper.response.EspecialidadeResponse;
import com.petcare.service.EspecialidadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
public class EspecialidadeController {

    private final EspecialidadeService especialidadeService;

    @GetMapping
    public ResponseEntity<List<EspecialidadeResponse>> getAllEspecialidades() {
        List<EspecialidadeResponse> especialidades = especialidadeService.getAllEspecialidades();
        return ResponseEntity.ok(especialidades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadeResponse> getEspecialidadeById(@PathVariable UUID id) {
        EspecialidadeResponse especialidade = especialidadeService.getEspecialidadeById(id);
        return ResponseEntity.ok(especialidade);
    }

    @PostMapping
    public ResponseEntity<EspecialidadeResponse> criarEspecialidade(@Valid @RequestBody EspecialidadeRequest especialidadeRequest) {
        EspecialidadeResponse especialidade = especialidadeService.criarEspecialidade(especialidadeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(especialidade);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadeResponse> atualizarEspecialidade(@PathVariable UUID id, @Valid @RequestBody EspecialidadeRequest especialidadeRequest) {
        EspecialidadeResponse especialidade = especialidadeService.atualizarEspecialidade(id, especialidadeRequest);
        return ResponseEntity.ok(especialidade);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEspecialidade(@PathVariable UUID id) {
        especialidadeService.deletarEspecialidade(id);
        return ResponseEntity.noContent().build();
    }
}