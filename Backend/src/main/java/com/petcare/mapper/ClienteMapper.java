package com.petcare.mapper;

import lombok.experimental.UtilityClass;
import com.petcare.model.Cliente;
import com.petcare.model.Pet;
import com.petcare.mapper.request.ClienteCreateRequest;
import com.petcare.mapper.request.ClienteUpdateRequest;
import com.petcare.mapper.request.PetRequest;
import com.petcare.mapper.response.ClienteResponse;

@UtilityClass
public class ClienteMapper {

    public static Cliente toCliente(ClienteCreateRequest clienteCreateRequest){
        return Cliente.builder()
                .cpf(clienteCreateRequest.cpf())
                .nomeCompleto(clienteCreateRequest.nomeCompleto())
                .dataNascimento(clienteCreateRequest.dataNascimento())
                .build();
    }

    public static void updateClienteFromRequest(ClienteUpdateRequest clienteUpdateRequest, Cliente cliente){
        cliente.setNomeCompleto(clienteUpdateRequest.nomeCompleto());
        cliente.setCpf(clienteUpdateRequest.cpf());
        cliente.setDataNascimento(clienteUpdateRequest.dataNascimento());
    }

    public static ClienteResponse toResponse(Cliente cliente){
        return ClienteResponse.builder()
                .id(cliente.getId())
                .nomeCompleto(cliente.getNomeCompleto())
                .cpf(cliente.getCpf())
                .dataNascimento(cliente.getDataNascimento())
                .build();
    }
}
