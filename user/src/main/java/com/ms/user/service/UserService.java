package com.ms.user.service;

import com.ms.user.dto.CreateUserDTO;
import com.ms.user.dto.UserResponseDTO;
import com.ms.user.entity.UserModel;
import com.ms.user.exception.ConflictException;
import com.ms.user.exception.EntityNotFoundException;
import com.ms.user.mapper.UserMapper;
import com.ms.user.producer.UserProducer;
import com.ms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProducer userProducer;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDTO save(CreateUserDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new ConflictException("O e-mail informado já está cadastrado.");
        }

        UserModel userModel = userMapper.toEntity(dto);
        userModel = userRepository.save(userModel);
        log.info("Usuário cadastrado com sucesso: {}", userModel.getUserId());

        userProducer.publishMessageEmail(userModel);
        return userMapper.toResponseDTO(userModel);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return userMapper.toResponseDTOList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(UUID userId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
        return userMapper.toResponseDTO(user);
    }
}

