package com.petcare.mapper;

import lombok.experimental.UtilityClass;
import com.petcare.model.Especialidade;
import com.petcare.mapper.request.EspecialidadeRequest;
import com.petcare.mapper.response.EspecialidadeResponse;

@UtilityClass
public class EspecialidadeMapper {

    public static Especialidade toEspecialidade(EspecialidadeRequest especialidadeRequest){
        return Especialidade.builder()
                .nome(especialidadeRequest.nome())
                .build();
    }

    public static EspecialidadeResponse toEspecialidadeResponse(Especialidade especialidade){
        return EspecialidadeResponse.builder()
                .id(especialidade.getId())
                .nome(especialidade.getNome())
                .build();
    }
}
