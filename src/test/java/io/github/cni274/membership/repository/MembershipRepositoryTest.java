package io.github.cni274.membership.repository;

import io.github.cni274.membership.entity.Membership;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static io.github.cni274.membership.enums.MembershipType.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class MembershipRepositoryTest {

    @Autowired
    private MembershipRepository membershipRepository;

    @Test
    @DisplayName("멤버십 등록 성공")
    void insertMembershipTest() {
        Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(NAVER)
                .point(10000)
                .build();

        Membership savedMembership = membershipRepository.save(membership);

        assertThat(savedMembership.getId()).isNotNull();
        assertThat(savedMembership.getUserId()).isEqualTo("userId");
        assertThat(savedMembership.getMembershipType()).isEqualTo(NAVER);
        assertThat(savedMembership.getPoint()).isEqualTo(10000);
    }

    @Test
    @DisplayName("멤버십 조회 성공 - userId, membershipType 이용")
    void existsMembershipTest() {
        Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(NAVER)
                .point(10000)
                .build();

        membershipRepository.save(membership);
        Membership findMembership = membershipRepository.findByUserIdAndMembershipType("userId", NAVER);

        assertThat(findMembership).isNotNull();
        assertThat(findMembership.getUserId()).isNotNull();
        assertThat(findMembership.getUserId()).isEqualTo("userId");
        assertThat(findMembership.getMembershipType()).isEqualTo(NAVER);
        assertThat(findMembership.getPoint()).isEqualTo(10000);
    }
}