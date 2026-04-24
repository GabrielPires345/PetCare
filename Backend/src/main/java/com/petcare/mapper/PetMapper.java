package com.petcare.mapper;

import com.petcare.mapper.response.PetResponse;
import lombok.experimental.UtilityClass;
import com.petcare.model.Pet;
import com.petcare.mapper.request.PetRequest;

@UtilityClass
public class PetMapper {

    public static Pet toPet(PetRequest petRequest){
        return Pet.builder()
                .nome(petRequest.nome())
                .especie(petRequest.especie())
                .sexo(petRequest.sexo())
                .peso(petRequest.peso())
                .dataNascimento(petRequest.dataNascimento())
                .castrado(petRequest.castrado())
                .build();
    }

    public static PetResponse toPetResponse(Pet pet){
        return PetResponse.builder()
                .id(pet.getId())
                .nome(pet.getNome())
                .especie(pet.getEspecie())
                .sexo(pet.getSexo())
                .peso(pet.getPeso())
                .dataNascimento(pet.getDataNascimento())
                .castrado(pet.getCastrado())
                .build();
    }
}
