package com.petcare.mapper;

import com.petcare.model.Telefone;
import com.petcare.mapper.request.TelefoneRequest;
import com.petcare.mapper.response.TelefoneResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TelefoneMapper {

    public static Telefone toTelefone(TelefoneRequest telefoneRequest){
        return Telefone.builder()
                .ddd(telefoneRequest.ddd())
                .numero(telefoneRequest.numero())
                .whatsapp(telefoneRequest.whatsapp())
                .build();
    }

    public static TelefoneResponse toTelefoneResponse(Telefone telefone){
        return TelefoneResponse.builder()
                .id(telefone.getId())
                .ddd(telefone.getDdd())
                .numero(telefone.getNumero())
                .whatsapp(telefone.getWhatsapp())
                .build();
    }
}
