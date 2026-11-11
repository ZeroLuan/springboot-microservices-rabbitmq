package com.ms.user.repository;

import com.ms.user.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {

    boolean existsByEmail(String email);
}

