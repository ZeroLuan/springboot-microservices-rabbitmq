package com.ms.user.mapper;

import com.ms.user.dto.CreateUserDTO;
import com.ms.user.dto.UserResponseDTO;
import com.ms.user.entity.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    UserModel toEntity(CreateUserDTO dto);

    UserResponseDTO toResponseDTO(UserModel entity);

    List<UserResponseDTO> toResponseDTOList(List<UserModel> entities);
}

