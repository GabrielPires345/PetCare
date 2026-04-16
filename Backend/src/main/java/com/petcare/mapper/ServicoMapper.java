package com.petcare.mapper;

import com.petcare.model.Servico;
import com.petcare.mapper.request.ServicoRequest;
import com.petcare.mapper.response.ServicoResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicoMapper {

    public static Servico toServico(ServicoRequest request) {
        return Servico.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .precoBase(request.precoBase())
                .duracaoMinutos(request.duracaoMinutos())
                .build();
    }

    public static ServicoResponse toServicoResponse(Servico servico) {
        return ServicoResponse.builder()
                .id(servico.getId())
                .nome(servico.getNome())
                .descricao(servico.getDescricao())
                .precoBase(servico.getPrecoBase())
                .duracaoMinutos(servico.getDuracaoMinutos())
                .build();
    }
}
