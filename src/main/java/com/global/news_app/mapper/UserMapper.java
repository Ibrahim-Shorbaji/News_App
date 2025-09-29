package com.global.news_app.mapper;

import com.global.news_app.models.User;
import com.global.news_app.payload.request.UserRequest;
import com.global.news_app.payload.response.UserResponse;

import java.util.stream.Collectors;

public class UserMapper {

    public static User toEntity(UserRequest dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setDateOfBirth(dto.getDateOfBirth());
        return user;
    }

    public static UserResponse toDto(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getRoles().stream()
                        .map(role -> role.getName().name()) // Role(name=ERole) → String
                        .collect(Collectors.toSet())
        );
    }
}
