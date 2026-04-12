package com.petcare.mapper;

import lombok.experimental.UtilityClass;
import com.petcare.model.Cliente;
import com.petcare.mapper.request.ClienteRequest;
import com.petcare.mapper.response.ClienteResponse;

@UtilityClass
public class ClienteMapper {

    public static Cliente toCliente(ClienteRequest clienteRequest){
        return Cliente.builder()
                .nomeCompleto(clienteRequest.nomeCompleto())
                .cpf(clienteRequest.cpf())
                .dataNascimento(clienteRequest.dataNascimento())
                .build();
    }

    public static ClienteResponse toClienteResponse(Cliente cliente){
        return ClienteResponse.builder()
                .id(cliente.getId())
                .nomeCompleto(cliente.getNomeCompleto())
                .cpf(cliente.getCpf())
                .dataNascimento(cliente.getDataNascimento())
                .build();
    }
}