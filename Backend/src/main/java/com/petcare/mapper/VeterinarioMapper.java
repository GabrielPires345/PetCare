package com.petcare.mapper;

import com.petcare.model.Especialidade;
import com.petcare.repository.EspecialidadeRepository;
import lombok.experimental.UtilityClass;
import com.petcare.model.Veterinario;
import com.petcare.mapper.request.VeterinarioRequest;
import com.petcare.mapper.response.VeterinarioResponse;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class VeterinarioMapper {

    public static Veterinario toVeterinario(VeterinarioRequest veterinarioRequest, EspecialidadeRepository especialidadeRepository){
        Set<Especialidade> especialidades = new HashSet<>();
        if (veterinarioRequest.especialidadeIds() != null) {
            especialidades = veterinarioRequest.especialidadeIds().stream()
                    .map(especialidadeRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
        }

        return Veterinario.builder()
                .nome(veterinarioRequest.nome())
                .crmv(veterinarioRequest.crmv())
                .especialidades(especialidades)
                .build();
    }

    public static VeterinarioResponse toVeterinarioResponse(Veterinario veterinario){
        return VeterinarioResponse.builder()
                .id(veterinario.getId())
                .nome(veterinario.getNome())
                .crmv(veterinario.getCrmv())
                .especialidadeIds(veterinario.getEspecialidades().stream()
                        .map(Especialidade::getId)
                        .collect(Collectors.toSet()))
                .build();
    }
}
