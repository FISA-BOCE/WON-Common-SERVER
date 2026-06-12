package com.woorifisa.won_common_server.domain.mapping.model;

import com.woorifisa.won_common_server.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
@Getter
@Table(name = "comm_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommUser extends BaseTimeEntity implements Persistable<UUID> {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "user_uuid", columnDefinition = "VARCHAR(36)")
    private UUID userUuid;

    @Column(name = "ci_hash", length = 255, unique = true)
    private String ciHash;

    private CommUser(String ciHash) {
        this.ciHash = ciHash;
    }

    public static CommUser create(UUID userUuid, String ciHash) {
        CommUser commUser = new CommUser(ciHash);
        commUser.userUuid = userUuid;
        return commUser;
    }

    @Override
    public UUID getId() {
        return userUuid;
    }

    @Override
    public boolean isNew() {
        return createdAt == null;
    }

}
