package com.woorifisa.won_common_server.domain.mapping.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.woorifisa.won_common_server.domain.mapping.dto.request.InitializeUserMappingRequest;
import com.woorifisa.won_common_server.domain.mapping.dto.request.UpdateCardUserMappingRequest;
import com.woorifisa.won_common_server.domain.mapping.dto.request.UpdateInvestUserMappingRequest;
import com.woorifisa.won_common_server.domain.mapping.dto.response.MappingStatusResponse;
import com.woorifisa.won_common_server.domain.mapping.exception.MappingErrorCode;
import com.woorifisa.won_common_server.domain.mapping.model.CommUser;
import com.woorifisa.won_common_server.domain.mapping.model.CommUserMapping;
import com.woorifisa.won_common_server.domain.mapping.repository.CommUserRepository;
import com.woorifisa.won_common_server.domain.mapping.repository.CommUserMappingRepository;
import com.woorifisa.won_common_server.global.exception.handler.BusinessException;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MappingServiceTest {

    @Mock
    private CommUserRepository commUserRepository;

    @Mock
    private CommUserMappingRepository commUserMappingRepository;

    @InjectMocks
    private MappingService mappingService;

    @Test
    @DisplayName("공통 사용자 매핑이 없으면 초기 row를 생성한다")
    void initializeUserMappingCreatesInitialMapping() throws Exception {
        UUID userUuid = UUID.fromString("0a31e4b1-2b1d-4b5e-8b82-0fb48e502111");
        InitializeUserMappingRequest request = new InitializeUserMappingRequest(userUuid);

        CommUser commUser = CommUser.create(userUuid, null);
        CommUserMapping mapping = newCommUserMapping(commUser);

        given(commUserMappingRepository.findByCommUserUserUuid(userUuid)).willReturn(Optional.empty());
        given(commUserRepository.findById(userUuid)).willReturn(Optional.empty());
        given(commUserRepository.save(any(CommUser.class))).willReturn(commUser);
        given(commUserMappingRepository.save(any(CommUserMapping.class))).willReturn(mapping);

        MappingStatusResponse response = mappingService.initializeUserMapping(request);

        assertThat(response.userUuid()).isEqualTo(userUuid);
        assertThat(response.card().isConnected()).isFalse();
        assertThat(response.invest().isConnected()).isFalse();
        verify(commUserRepository).save(any(CommUser.class));
        verify(commUserMappingRepository).save(any(CommUserMapping.class));
    }

    @Test
    @DisplayName("공통 사용자 매핑이 이미 있으면 기존 상태를 반환한다")
    void initializeUserMappingReturnsExistingMapping() throws Exception {
        UUID userUuid = UUID.fromString("0a31e4b1-2b1d-4b5e-8b82-0fb48e502111");
        InitializeUserMappingRequest request = new InitializeUserMappingRequest(userUuid);
        CommUser commUser = newCommUser(userUuid, "test-ci-hash-001");
        CommUserMapping mapping = newCommUserMapping(commUser);

        given(commUserMappingRepository.findByCommUserUserUuid(userUuid)).willReturn(Optional.of(mapping));

        MappingStatusResponse response = mappingService.initializeUserMapping(request);

        assertThat(response.userUuid()).isEqualTo(userUuid);
        assertThat(response.card().isConnected()).isFalse();
        assertThat(response.invest().isConnected()).isFalse();
    }

    @Test
    @DisplayName("카드/증권 매핑 여부를 조회한다 - 카드 미연결, 증권 미연결")
    void getMappingStatusCardNoneInvestNone() throws Exception {
        // given
        UUID userUuid = UUID.fromString("0a31e4b1-2b1d-4b5e-8b82-0fb48e502111");

        CommUser commUser = newCommUser(userUuid, "test-ci-hash-001");
        CommUserMapping mapping = newCommUserMapping(commUser);

        given(commUserMappingRepository.findByCommUserUserUuid(userUuid))
                .willReturn(Optional.of(mapping));

        // when
        MappingStatusResponse response = mappingService.getMappingStatus(userUuid);

        // then
        assertThat(response.userUuid()).isEqualTo(userUuid);

        assertThat(response.card().cardUserUuid()).isNull();
        assertThat(response.card().isConnected()).isFalse();

        assertThat(response.invest().investUserUuid()).isNull();
        assertThat(response.invest().isConnected()).isFalse();
    }

    @Test
    @DisplayName("카드/증권 매핑 여부를 조회한다 - 카드 연결, 증권 미연결")
    void getMappingStatusCardLinkInvestNone() throws Exception {
        // given
        UUID userUuid = UUID.fromString("0a31e4b1-2b1d-4b5e-8b82-0fb48e502111");
        UUID cardUserUuid = UUID.fromString("4f8b3f2a-f7e6-43f8-b4df-a6729a671111");

        CommUser commUser = newCommUser(userUuid, "test-ci-hash-001");
        CommUserMapping mapping = newCommUserMapping(commUser);
        mapping.linkCardUser(cardUserUuid);

        given(commUserMappingRepository.findByCommUserUserUuid(userUuid))
                .willReturn(Optional.of(mapping));

        // when
        MappingStatusResponse response = mappingService.getMappingStatus(userUuid);

        // then
        assertThat(response.userUuid()).isEqualTo(userUuid);

        assertThat(response.card().cardUserUuid()).isEqualTo(cardUserUuid);
        assertThat(response.card().isConnected()).isTrue();

        assertThat(response.invest().investUserUuid()).isNull();
        assertThat(response.invest().isConnected()).isFalse();
    }

    @Test
    @DisplayName("카드/증권 매핑 여부를 조회한다 - 카드 미연결, 증권 연결")
    void getMappingStatusCardNoneInvestLink() throws Exception {
        // given
        UUID userUuid = UUID.fromString("0a31e4b1-2b1d-4b5e-8b82-0fb48e502111");
        UUID investUserUuid = UUID.fromString("cc1e08f6-f8ea-4b3e-8e41-85f94b473111");

        CommUser commUser = newCommUser(userUuid, "test-ci-hash-001");
        CommUserMapping mapping = newCommUserMapping(commUser);
        mapping.linkInvestUser(investUserUuid);

        given(commUserMappingRepository.findByCommUserUserUuid(userUuid))
                .willReturn(Optional.of(mapping));

        // when
        MappingStatusResponse response = mappingService.getMappingStatus(userUuid);

        // then
        assertThat(response.userUuid()).isEqualTo(userUuid);

        assertThat(response.card().cardUserUuid()).isNull();
        assertThat(response.card().isConnected()).isFalse();

        assertThat(response.invest().investUserUuid()).isEqualTo(investUserUuid);
        assertThat(response.invest().isConnected()).isTrue();
    }

    @Test
    @DisplayName("카드/증권 매핑 여부를 조회한다 - 카드 연결, 증권 연결")
    void getMappingStatusCardLinkInvestLink() throws Exception {
        // given
        UUID userUuid = UUID.fromString("0a31e4b1-2b1d-4b5e-8b82-0fb48e502111");
        UUID cardUserUuid = UUID.fromString("4f8b3f2a-f7e6-43f8-b4df-a6729a671111");
        UUID investUserUuid = UUID.fromString("cc1e08f6-f8ea-4b3e-8e41-85f94b473111");

        CommUser commUser = newCommUser(userUuid, "test-ci-hash-001");
        CommUserMapping mapping = newCommUserMapping(commUser);
        mapping.linkCardUser(cardUserUuid);
        mapping.linkInvestUser(investUserUuid);

        given(commUserMappingRepository.findByCommUserUserUuid(userUuid))
                .willReturn(Optional.of(mapping));

        // when
        MappingStatusResponse response = mappingService.getMappingStatus(userUuid);

        // then
        assertThat(response.userUuid()).isEqualTo(userUuid);

        assertThat(response.card().cardUserUuid()).isEqualTo(cardUserUuid);
        assertThat(response.card().isConnected()).isTrue();

        assertThat(response.invest().investUserUuid()).isEqualTo(investUserUuid);
        assertThat(response.invest().isConnected()).isTrue();
    }

    @Test
    @DisplayName("카드 사용자 UUID를 매핑에 연결한다")
    void updateCardUserMapping() throws Exception {
        // given
        UUID userUuid = UUID.fromString("0a31e4b1-2b1d-4b5e-8b82-0fb48e502111");
        UUID cardUserUuid = UUID.fromString("4f8b3f2a-f7e6-43f8-b4df-a6729a671111");

        CommUser commUser = newCommUser(userUuid, "test-ci-hash-001");
        CommUserMapping mapping = newCommUserMapping(commUser);
        UpdateCardUserMappingRequest request = new UpdateCardUserMappingRequest(cardUserUuid);

        given(commUserMappingRepository.findByCommUserUserUuidForUpdate(userUuid))
                .willReturn(Optional.of(mapping));

        given(commUserMappingRepository.existsByCardUserUuid(cardUserUuid))
                .willReturn(false);

        // when
        MappingStatusResponse response = mappingService.updateCardUserMapping(userUuid, request);

        // then
        assertThat(response.userUuid()).isEqualTo(userUuid);

        assertThat(response.card().cardUserUuid()).isEqualTo(cardUserUuid);
        assertThat(response.card().isConnected()).isTrue();

        assertThat(response.invest().investUserUuid()).isNull();
        assertThat(response.invest().isConnected()).isFalse();
    }

    @Test
    @DisplayName("매핑 정보가 없으면 예외가 발생한다")
    void getMappingStatusMappingNotFound() {
        // given
        UUID userUuid = UUID.fromString("0a311411-2b1d-4b5e-8b82-0fb48e511111");

        given(commUserMappingRepository.findByCommUserUserUuid(userUuid))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> mappingService.getMappingStatus(userUuid))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode())
                            .isEqualTo(MappingErrorCode.MAPPING_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("이미 카드 사용자와 연결되어 있으면 예외가 발생한다")
    void updateCardUserAlreadyLinked() throws Exception {
        // given
        UUID userUuid = UUID.fromString("0a31e4b1-2b1d-4b5e-8b82-0fb48e502111");
        UUID cardUserUuid = UUID.fromString("4f8b3f2a-f7e6-43f8-b4df-a6729a671111");

        CommUser commUser = newCommUser(userUuid, "test-ci-hash-001");
        CommUserMapping mapping = newCommUserMapping(commUser);
        mapping.linkCardUser(cardUserUuid);

        UpdateCardUserMappingRequest request = new UpdateCardUserMappingRequest(cardUserUuid);

        given(commUserMappingRepository.findByCommUserUserUuidForUpdate(userUuid))
                .willReturn(Optional.of(mapping));

        // when & then
        assertThatThrownBy(() -> mappingService.updateCardUserMapping(userUuid, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode())
                            .isEqualTo(MappingErrorCode.CARD_USER_ALREADY_LINKED);
                });
    }

    @Test
    @DisplayName("증권 사용자 UUID를 매핑에 연결한다")
    void updateInvestUser() throws Exception {
        // given
        UUID userUuid = UUID.fromString("0a31e4b1-2b1d-4b5e-8b82-0fb48e502111");
        UUID investUserUuid = UUID.fromString("4f8b3f2a-f7e6-43f8-b4df-a6729a671111");

        CommUser commUser = newCommUser(userUuid, "test-ci-hash-001");
        CommUserMapping mapping = newCommUserMapping(commUser);
        UpdateInvestUserMappingRequest request = new UpdateInvestUserMappingRequest(investUserUuid);

        given(commUserMappingRepository.findByCommUserUserUuidForUpdate(userUuid))
                .willReturn(Optional.of(mapping));

        given(commUserMappingRepository.existsByInvestUserUuid(investUserUuid))
                .willReturn(false);

        // when
        MappingStatusResponse response = mappingService.updateInvestUserMapping(userUuid, request);

        // then
        assertThat(response.userUuid()).isEqualTo(userUuid);

        assertThat(response.card().cardUserUuid()).isNull();
        assertThat(response.card().isConnected()).isFalse();

        assertThat(response.invest().investUserUuid()).isEqualTo(investUserUuid);
        assertThat(response.invest().isConnected()).isTrue();
    }

    @Test
    @DisplayName("이미 증권 사용자와 연결되어 있으면 예외가 발생한다")
    void updateInvestUserAlreadyLinked() throws Exception {
        // given
        UUID userUuid = UUID.fromString("0a31e4b1-2b1d-4b5e-8b82-0fb48e502111");
        UUID investUserUuid = UUID.fromString("4f8b3f2a-f7e6-43f8-b4df-a6729a671111");

        CommUser commUser = newCommUser(userUuid, "test-ci-hash-001");
        CommUserMapping mapping = newCommUserMapping(commUser);
        mapping.linkInvestUser(investUserUuid);

        UpdateInvestUserMappingRequest request = new UpdateInvestUserMappingRequest(investUserUuid);

        given(commUserMappingRepository.findByCommUserUserUuidForUpdate(userUuid))
                .willReturn(Optional.of(mapping));

        // when & then
        assertThatThrownBy(() -> mappingService.updateInvestUserMapping(userUuid, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode())
                            .isEqualTo(MappingErrorCode.INVEST_USER_ALREADY_LINKED);
                });
    }

    @Test
    @DisplayName("이미 다른 사용자에게 연결된 cardUserUuid이면 예외가 발생한다")
    void updateCardUserMappingAlreadyMapped() throws Exception {
        // given
        UUID userUuid = UUID.fromString("0a31e4b1-2b1d-4b5e-8b82-0fb48e502111");
        UUID cardUserUuid = UUID.fromString("4f8b3f2a-f7e6-43f8-b4df-a6729a671111");

        CommUser commUser = newCommUser(userUuid, "test-ci-hash-001");
        CommUserMapping mapping = newCommUserMapping(commUser);

        UpdateCardUserMappingRequest request = new UpdateCardUserMappingRequest(cardUserUuid);

        given(commUserMappingRepository.findByCommUserUserUuidForUpdate(userUuid))
                .willReturn(Optional.of(mapping));

        given(commUserMappingRepository.existsByCardUserUuid(cardUserUuid))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> mappingService.updateCardUserMapping(userUuid, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode())
                            .isEqualTo(MappingErrorCode.CARD_USER_ALREADY_MAPPED);
                });
    }

    @Test
    @DisplayName("이미 다른 사용자에게 연결된 investUserUuid이면 예외가 발생한다")
    void updateInvestUserMappingAlreadyMapped() throws Exception {
        // given
        UUID userUuid = UUID.fromString("0a31e4b1-2b1d-4b5e-8b82-0fb48e502111");
        UUID investUserUuid = UUID.fromString("cc1e08f6-f8ea-4b3e-8e41-85f94b473111");

        CommUser commUser = newCommUser(userUuid, "test-ci-hash-001");
        CommUserMapping mapping = newCommUserMapping(commUser);

        UpdateInvestUserMappingRequest request = new UpdateInvestUserMappingRequest(investUserUuid);

        given(commUserMappingRepository.findByCommUserUserUuidForUpdate(userUuid))
                .willReturn(Optional.of(mapping));

        given(commUserMappingRepository.existsByInvestUserUuid(investUserUuid))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> mappingService.updateInvestUserMapping(userUuid, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode())
                            .isEqualTo(MappingErrorCode.INVEST_USER_ALREADY_MAPPED);
                });
    }

    private CommUser newCommUser(UUID userUuid, String ciHash) throws Exception {
        return CommUser.create(userUuid, ciHash);
    }

    private CommUserMapping newCommUserMapping(CommUser commUser) {
        return CommUserMapping.create(commUser);
    }

}
