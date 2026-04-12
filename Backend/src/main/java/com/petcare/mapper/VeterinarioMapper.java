package com.petcare.mapper;

import lombok.experimental.UtilityClass;
import com.petcare.model.Veterinario;
import com.petcare.mapper.request.VeterinarioRequest;
import com.petcare.mapper.response.VeterinarioResponse;

import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class VeterinarioMapper {

    public static Veterinario toVeterinario(VeterinarioRequest veterinarioRequest){
        return Veterinario.builder()
                .nome(veterinarioRequest.nome())
                .crmv(veterinarioRequest.crmv())
                .build();
    }

    public static VeterinarioResponse toVeterinarioResponse(Veterinario veterinario){
        return VeterinarioResponse.builder()
                .id(veterinario.getId())
                .nome(veterinario.getNome())
                .crmv(veterinario.getCrmv())
                .especialidadeIds(veterinario.getEspecialidades().stream()
                        .map(especialidade -> especialidade.getId())
                        .collect(Collectors.toSet()))
                .build();
    }
}