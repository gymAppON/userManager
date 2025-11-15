package com.example.userManager.user;

import com.example.userManager.config.CustomMapperConfig;
import com.example.userManager.dto.request.UserRequestDto;
import com.example.userManager.dto.request.auth.SignupRequestDto;
import com.example.userManager.dto.response.UserResponseDto;
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

//    @Mapping(target = "id", ignore = true)
//    void updateUserFromDto(UserRequestDto request, @MappingTarget UserEntity userEntity);
}
