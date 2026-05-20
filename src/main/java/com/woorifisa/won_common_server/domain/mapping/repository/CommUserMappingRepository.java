package com.woorifisa.won_common_server.domain.mapping.repository;

import com.woorifisa.won_common_server.domain.mapping.model.CommUserMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommUserMappingRepository extends JpaRepository<CommUserMapping, UUID> {
    Optional<CommUserMapping> findByCommUserUserUuid(UUID userUuid);
}
