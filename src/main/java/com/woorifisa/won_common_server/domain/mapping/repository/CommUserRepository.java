package com.woorifisa.won_common_server.domain.mapping.repository;

import com.woorifisa.won_common_server.domain.mapping.model.CommUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommUserRepository extends JpaRepository<CommUser, Long> {
}
