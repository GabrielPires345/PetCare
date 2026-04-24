package com.petcare.mapper;

import lombok.experimental.UtilityClass;
import com.petcare.model.Usuario;
import com.petcare.mapper.request.UserRequest;
import com.petcare.mapper.response.UserResponse;

@UtilityClass
public class UserMapper {

    public static Usuario toUser(UserRequest userRequest){
        return Usuario.builder()
                .email(userRequest.email())
                .senhaHash(userRequest.senha()) // Note: In real app, password should be hashed
                .nivelAcesso(userRequest.nivelAcesso())
                .build();
    }

    public static UserResponse toUserResponse(Usuario user){
        return UserResponse.builder()
                .id(user.getId())
                .nomeUsuario(user.getNomeUsuario())
                .email(user.getEmail())
                .nivelAcesso(user.getNivelAcesso())
                .build();
    }
}
