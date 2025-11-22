package com.example.userManager.domain.user;

import com.example.userManager.infrastructure.config.CustomMapperConfig;
import com.example.userManager.domain.user.dto.UserRequestDto;
import com.example.userManager.domain.auth.dto.SignupRequestDto;
import com.example.userManager.domain.user.dto.UserResponseDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", config = CustomMapperConfig.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    UserEntity toEntity(UserRequestDto request);

    @Mapping(target = "id", ignore = true)
    UserEntity toEntity(SignupRequestDto request);

    UserResponseDto toResponse(UserEntity userEntity);

    List<UserResponseDto> toResponseDtoList(List<UserEntity> entities);
    List<UserEntity> toEntityList(List<UserResponseDto> dtos);
}
