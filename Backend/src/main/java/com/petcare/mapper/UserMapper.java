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
                .passwordHash(userRequest.password()) // Note: In real app, password should be hashed
                .nivelAcesso("CLIENTE") // Default role, can be adjusted based on context
                .build();
    }

    public static UserResponse toUserResponse(Usuario user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nivelAcesso(user.getNivelAcesso())
                .build();
    }
}