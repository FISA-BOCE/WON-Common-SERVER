package com.woorifisa.won_common_server.domain.mapping.repository;

import com.woorifisa.won_common_server.domain.mapping.model.CommUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommUserRepository extends JpaRepository<CommUser, UUID> {
}
