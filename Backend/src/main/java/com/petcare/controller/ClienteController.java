package com.petcare.controller;

import com.petcare.mapper.request.ClienteCreateRequest;
import com.petcare.mapper.request.ClienteUpdateRequest;
import com.petcare.mapper.request.EnderecoRequest;
import com.petcare.mapper.request.TelefoneRequest;
import com.petcare.mapper.response.ClienteResponse;
import com.petcare.mapper.response.EnderecoResponse;
import com.petcare.mapper.response.TelefoneResponse;
import com.petcare.service.ClienteService;
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
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;
    private final EnderecoService enderecoService;
    private final TelefoneService telefoneService;

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> getAllClientes() {
        List<ClienteResponse> clientes = clienteService.getAllClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> getClienteById(@PathVariable UUID id) {
        ClienteResponse cliente = clienteService.getClienteById(id);
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> updateCliente(@PathVariable UUID id, @Valid @RequestBody ClienteUpdateRequest clienteUpdateRequest) {
        ClienteResponse cliente = clienteService.updateCliente(id, clienteUpdateRequest);
        return ResponseEntity.ok(cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable UUID id) {
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }

    // Endereços do cliente
    @PostMapping("/{id}/enderecos")
    public ResponseEntity<EnderecoResponse> adicionarEndereco(
            @PathVariable UUID id,
            @Valid @RequestBody EnderecoRequest enderecoRequest) {
        EnderecoResponse endereco = enderecoService.criarEnderecoParaCliente(id, enderecoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(endereco);
    }

    @GetMapping("/{id}/enderecos")
    public ResponseEntity<List<EnderecoResponse>> listarEnderecos(@PathVariable UUID id) {
        List<EnderecoResponse> enderecos = enderecoService.getEnderecosByClienteId(id);
        return ResponseEntity.ok(enderecos);
    }

    // Telefones do cliente
    @PostMapping("/{id}/telefones")
    public ResponseEntity<TelefoneResponse> adicionarTelefone(
            @PathVariable UUID id,
            @Valid @RequestBody TelefoneRequest telefoneRequest) {
        TelefoneResponse telefone = telefoneService.criarTelefoneParaCliente(id, telefoneRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(telefone);
    }

    @GetMapping("/{id}/telefones")
    public ResponseEntity<List<TelefoneResponse>> listarTelefones(@PathVariable UUID id) {
        List<TelefoneResponse> telefones = telefoneService.getTelefonesByClienteId(id);
        return ResponseEntity.ok(telefones);
    }
}