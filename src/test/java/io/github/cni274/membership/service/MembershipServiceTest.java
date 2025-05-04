package io.github.cni274.membership.service;

import io.github.cni274.membership.dto.MembershipResponse;
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


        MembershipResponse addedMembership = target.addMembership(userId, membershipType, point);

        assertThat(addedMembership.getId()).isNotNull();
        assertThat(addedMembership.getMembershipType()).isEqualTo(membershipType);

        verify(membershipRepository, times(1)).findByUserIdAndMembershipType(userId, membershipType);
        verify(membershipRepository, times(1)).save(any(Membership.class));
    }

    private Membership membership() {
        return Membership.builder()
                .id(-1L)
                .userId(userId)
                .membershipType(membershipType)
                .point(point)
                .build();
    }
}