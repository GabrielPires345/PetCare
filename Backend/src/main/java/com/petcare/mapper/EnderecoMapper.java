package com.petcare.mapper;

import com.petcare.model.Endereco;
import com.petcare.mapper.request.EnderecoRequest;
import com.petcare.mapper.response.EnderecoResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EnderecoMapper {

    public static Endereco toEndereco(EnderecoRequest enderecoRequest){
        return Endereco.builder()
                .logradouro(enderecoRequest.logradouro())
                .numero(enderecoRequest.numero())
                .bairro(enderecoRequest.bairro())
                .cidade(enderecoRequest.cidade())
                .uf(enderecoRequest.uf())
                .cep(enderecoRequest.cep())
                .build();
    }

    public static EnderecoResponse toEnderecoResponse(Endereco endereco){
        return EnderecoResponse.builder()
                .id(endereco.getId())
                .logradouro(endereco.getLogradouro())
                .numero(endereco.getNumero())
                .bairro(endereco.getBairro())
                .cidade(endereco.getCidade())
                .uf(endereco.getUf())
                .cep(endereco.getCep())
                .build();
    }
}
