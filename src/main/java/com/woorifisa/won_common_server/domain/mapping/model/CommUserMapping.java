package com.woorifisa.won_common_server.domain.mapping.model;

import com.woorifisa.won_common_server.domain.mapping.model.enums.LinkStatus;
import com.woorifisa.won_common_server.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommUserMapping extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    private Long mappingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", nullable = false, unique = true)
    private CommUser commUser;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(
            name = "card_user_uuid",
            columnDefinition = "VARCHAR(36)",
            unique = true
    )
    private UUID cardUserUuid;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(
            name = "invest_user_uuid",
            columnDefinition = "VARCHAR(36)",
            unique = true
    )
    private UUID investUserUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_link_status", nullable = false, length = 30)
    private LinkStatus cardLinkStatus = LinkStatus.NONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "invest_link_status", nullable = false, length = 30)
    private LinkStatus investLinkStatus = LinkStatus.NONE;

    private CommUserMapping(CommUser commUser) {
        this.commUser = commUser;
    }

    public static CommUserMapping create(CommUser commUser) {
        return new CommUserMapping(commUser);
    }

    public void linkCardUser(UUID cardUserUuid) {
        this.cardUserUuid = cardUserUuid;
        this.cardLinkStatus = LinkStatus.LINKED;
    }

    public void linkInvestUser(UUID investUserUuid) {
        this.investUserUuid = investUserUuid;
        this.investLinkStatus = LinkStatus.LINKED;
    }

    public boolean isCardConnected() {
        return this.cardUserUuid != null && this.cardLinkStatus == LinkStatus.LINKED;
    }

    public boolean isInvestConnected() {
        return this.investUserUuid != null && this.investLinkStatus == LinkStatus.LINKED;
    }

}
