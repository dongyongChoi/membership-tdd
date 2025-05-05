package io.github.cni274.membership.service;

import io.github.cni274.membership.dto.MembershipAddResponse;
import io.github.cni274.membership.dto.MembershipDetailResponse;
import io.github.cni274.membership.entity.Membership;
import io.github.cni274.membership.enums.MembershipErrorResult;
import io.github.cni274.membership.enums.MembershipType;
import io.github.cni274.membership.exception.MembershipException;
import io.github.cni274.membership.repository.MembershipRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    @Mock
    MembershipRepository membershipRepository;

    @InjectMocks
    MembershipService target;

    private final String userId = "userId";
    private final MembershipType membershipType = MembershipType.NAVER;
    private final Integer point = 10000;

    @Test
    @DisplayName("멤버십 등록 실패 - 이미 존재함")
    void failedToAddMembership() {
        doReturn(Membership.builder().build())
                .when(membershipRepository)
                .findByUserIdAndMembershipType(userId, membershipType);

        MembershipException result = assertThrows(MembershipException.class, () -> target.addMembership(userId, membershipType, point));

        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
    }

    @Test
    @DisplayName("멤버십 등록 성공")
    void successfulAddMembership() {
//        when(membershipRepository.findByUserIdAndMembershipType(userId, membershipType)).thenReturn(null);
        doReturn(null).when(membershipRepository).findByUserIdAndMembershipType(userId, membershipType);
        doReturn(membership()).when(membershipRepository).save(any(Membership.class));


        MembershipAddResponse addedMembership = target.addMembership(userId, membershipType, point);

        assertThat(addedMembership.getId()).isNotNull();
        assertThat(addedMembership.getMembershipType()).isEqualTo(membershipType);

        verify(membershipRepository, times(1)).findByUserIdAndMembershipType(userId, membershipType);
        verify(membershipRepository, times(1)).save(any(Membership.class));
    }

    @Test
    @DisplayName("멤버십 조회 성공 - 조회 3건이 발생")
    void successful_findMembership() {
        doReturn(List.of(
                membership(-1, "userId", MembershipType.NAVER, 10001),
                membership(-2, "userId", MembershipType.KAKAO, 10002),
                membership(-3, "userId", MembershipType.LINE, 10003)
        ))
                .when(membershipRepository)
                .findAllByUserId(userId);

        List<MembershipDetailResponse> membershipList = target.getMembershipList(userId);

        assertThat(membershipList.size()).isEqualTo(3);

        verify(membershipRepository).findAllByUserId(userId);
    }

    @Test
    @DisplayName("멤버십 상세 조회 실패 - 존재하지 않음")
    void failed_notFoundMembership() {
        doReturn(Optional.empty()).when(membershipRepository).findById(-1L);

        MembershipException membershipException = assertThrows(MembershipException.class, () -> target.getMembership(-1L, userId));

        assertThat(membershipException.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);

        verify(membershipRepository).findById(-1L);
    }
    @Test
    @DisplayName("멤버십 상세 조회 실패 - 본인이 아님")
    void failed_notMembershipOwner() {
        doReturn(Optional.of(Membership.builder()
                .id(-1L)
                .userId("otherId")
                .build()
        ))
                .when(membershipRepository).findById(-1L);

        MembershipException membershipException = assertThrows(MembershipException.class, () -> target.getMembership(-1L, userId));

        assertThat(membershipException.getErrorResult()).isEqualTo(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);

        verify(membershipRepository).findById(-1L);
    }

    @Test
    @DisplayName("멤버십 상세 조회 성공")
    void successful_findMembershipDetail() {
        doReturn(Optional.of(membership())).when(membershipRepository).findById(-1L);

        MembershipDetailResponse membershipDetailResponse = target.getMembership(-1L, userId);

        assertThat(membershipDetailResponse).isNotNull();
        assertThat(membershipDetailResponse.getId()).isEqualTo(-1L);
        assertThat(membershipDetailResponse.getMembershipType()).isEqualTo(membershipType);
        assertThat(membershipDetailResponse.getPoint()).isEqualTo(point);

        verify(membershipRepository).findById(-1L);
    }

    private Membership membership() {
        return Membership.builder()
                .id(-1L)
                .userId(userId)
                .membershipType(membershipType)
                .point(point)
                .build();
    }

    private Membership membership(long id, String userId, MembershipType membershipType, Integer point) {
        return Membership.builder()
                .id(id)
                .userId(userId)
                .membershipType(membershipType)
                .point(point)
                .createdAt(LocalDateTime.now())
                .build();
    }

}