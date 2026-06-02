package com.woorifisa.won_common_server.domain.mapping.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.woorifisa.won_common_server.domain.mapping.model.CommUser;
import com.woorifisa.won_common_server.domain.mapping.model.CommUserMapping;
import java.util.Optional;
import java.util.UUID;

import com.woorifisa.won_common_server.global.config.JpaConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
class CommUserMappingRepositoryTest {

    @Autowired
    private CommUserRepository commUserRepository;

    @Autowired
    private CommUserMappingRepository commUserMappingRepository;

    @Test
    @DisplayName("외부에서 지정한 userUuid로 신규 공통 사용자를 저장한다")
    void saveCommUserWithAssignedUserUuid() {
        // given
        UUID userUuid = UUID.fromString("8d619c85-b4f6-49f5-b622-88daa3b2b512");

        // when
        commUserRepository.saveAndFlush(CommUser.create(userUuid, null));

        // then
        assertThat(commUserRepository.findById(userUuid)).isPresent();
    }

    @Test
    @DisplayName("userUuid 기준으로 고객 매핑 정보를 조회한다")
    void findByCommUserUserUuid() {
        // given
        CommUser commUser = CommUser.create(UUID.randomUUID(), "test-ci-hash-001");
        CommUser savedUser = commUserRepository.save(commUser);

        CommUserMapping mapping = CommUserMapping.create(savedUser);
        CommUserMapping savedMapping = commUserMappingRepository.save(mapping);

        // when
        Optional<CommUserMapping> result =
                commUserMappingRepository.findByCommUserUserUuid(savedUser.getUserUuid());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getMappingId()).isEqualTo(savedMapping.getMappingId());
        assertThat(result.get().getCommUser().getUserUuid()).isEqualTo(savedUser.getUserUuid());

        assertThat(result.get().getCardUserUuid()).isNull();
        assertThat(result.get().isCardConnected()).isFalse();

        assertThat(result.get().getInvestUserUuid()).isNull();
        assertThat(result.get().isInvestConnected()).isFalse();
    }

    @Test
    @DisplayName("카드 사용자와 증권 사용자가 연결된 매핑 정보를 조회한다")
    void findLinkedMappingByCommUserUserUuid() {
        // given
        UUID cardUserUuid = UUID.fromString("4f8b3f2a-f7e6-43f8-b4df-a6729a671111");
        UUID investUserUuid = UUID.fromString("cc1e08f6-f8ea-4b3e-8e41-85f94b473111");

        CommUser commUser = CommUser.create(UUID.randomUUID(), "test-ci-hash-002");
        CommUser savedUser = commUserRepository.save(commUser);

        CommUserMapping mapping = CommUserMapping.create(savedUser);
        mapping.linkCardUser(cardUserUuid);
        mapping.linkInvestUser(investUserUuid);

        commUserMappingRepository.save(mapping);

        // when
        Optional<CommUserMapping> result =
                commUserMappingRepository.findByCommUserUserUuid(savedUser.getUserUuid());

        // then
        assertThat(result).isPresent();

        assertThat(result.get().getCardUserUuid()).isEqualTo(cardUserUuid);
        assertThat(result.get().isCardConnected()).isTrue();

        assertThat(result.get().getInvestUserUuid()).isEqualTo(investUserUuid);
        assertThat(result.get().isInvestConnected()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 userUuid로 조회하면 빈 Optional을 반환한다")
    void findByCommUserUserUuidNotFound() {
        // given
        UUID notExistsUserUuid = UUID.fromString("11111111-1111-1111-1111-111111111111");

        // when
        Optional<CommUserMapping> result =
                commUserMappingRepository.findByCommUserUserUuid(notExistsUserUuid);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cardUserUuid가 이미 존재하면 true를 반환한다")
    void existsByCardUserUuid() {
        // given
        UUID cardUserUuid = UUID.fromString("4f8b3f2a-f7e6-43f8-b4df-a6729a671111");

        CommUser commUser = CommUser.create(UUID.randomUUID(), "test-ci-hash-card-exists");
        CommUser savedUser = commUserRepository.save(commUser);

        CommUserMapping mapping = CommUserMapping.create(savedUser);
        mapping.linkCardUser(cardUserUuid);

        commUserMappingRepository.save(mapping);

        // when
        boolean result = commUserMappingRepository.existsByCardUserUuid(cardUserUuid);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("cardUserUuid가 존재하지 않으면 false를 반환한다")
    void existsByCardUserUuidNotFound() {
        // given
        UUID cardUserUuid = UUID.fromString("99999999-9999-9999-9999-999999999999");

        // when
        boolean result = commUserMappingRepository.existsByCardUserUuid(cardUserUuid);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("investUserUuid가 이미 존재하면 true를 반환한다")
    void existsByInvestUserUuid() {
        // given
        UUID investUserUuid = UUID.fromString("cc1e08f6-f8ea-4b3e-8e41-85f94b473111");

        CommUser commUser = CommUser.create(UUID.randomUUID(), "test-ci-hash-invest-exists");
        CommUser savedUser = commUserRepository.save(commUser);

        CommUserMapping mapping = CommUserMapping.create(savedUser);
        mapping.linkInvestUser(investUserUuid);

        commUserMappingRepository.save(mapping);

        // when
        boolean result = commUserMappingRepository.existsByInvestUserUuid(investUserUuid);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("investUserUuid가 존재하지 않으면 false를 반환한다")
    void existsByInvestUserUuidNotFound() {
        // given
        UUID investUserUuid = UUID.fromString("88888888-8888-8888-8888-888888888888");

        // when
        boolean result = commUserMappingRepository.existsByInvestUserUuid(investUserUuid);

        // then
        assertThat(result).isFalse();
    }
}
