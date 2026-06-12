package com.woorifisa.won_common_server.domain.mapping.repository;

import com.woorifisa.won_common_server.domain.mapping.model.CommUserMapping;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CommUserMappingRepository extends JpaRepository<CommUserMapping, Long> {

    Optional<CommUserMapping> findByCommUserUserUuid(UUID userUuid);

    boolean existsByCardUserUuid(UUID cardUserUuid);

    boolean existsByInvestUserUuid(UUID investUserUuid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select m
            from CommUserMapping m
            where m.commUser.userUuid = :userUuid
            """)
    Optional<CommUserMapping> findByCommUserUserUuidForUpdate(@Param("userUuid") UUID userUuid);

}
