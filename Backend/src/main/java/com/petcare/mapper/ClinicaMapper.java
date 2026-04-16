package com.petcare.mapper;

import lombok.experimental.UtilityClass;
import com.petcare.model.Clinica;
import com.petcare.mapper.request.ClinicaRequest;
import com.petcare.mapper.response.ClinicaResponse;

@UtilityClass
public class ClinicaMapper {

    public static Clinica toClinica(ClinicaRequest clinicaRequest){
        return Clinica.builder()
                .nomeClinica(clinicaRequest.nomeClinica())
                .razaoSocial(clinicaRequest.razaoSocial())
                .cnpj(clinicaRequest.cnpj())
                .build();
    }

    public static ClinicaResponse toClinicaResponse(Clinica clinica){
        return ClinicaResponse.builder()
                .id(clinica.getId())
                .nomeClinica(clinica.getNomeClinica())
                .razaoSocial(clinica.getRazaoSocial())
                .cnpj(clinica.getCnpj())
                .build();
    }
}
