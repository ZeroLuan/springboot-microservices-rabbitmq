package com.ms.email.mapper;

import com.ms.email.dto.EmailMessageDTO;
import com.ms.email.dto.EmailResponseDTO;
import com.ms.email.entity.EmailModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmailMapper {

    @Mapping(target = "emailId", ignore = true)
    @Mapping(target = "emailFrom", ignore = true)
    @Mapping(target = "sendDateEmail", ignore = true)
    @Mapping(target = "statusEmail", ignore = true)
    EmailModel toEntity(EmailMessageDTO dto);

    EmailResponseDTO toResponseDTO(EmailModel entity);

    List<EmailResponseDTO> toResponseDTOList(List<EmailModel> entities);
}

