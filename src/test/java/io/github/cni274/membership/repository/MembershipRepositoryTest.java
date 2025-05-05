package io.github.cni274.membership.repository;

import io.github.cni274.membership.entity.Membership;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static io.github.cni274.membership.enums.MembershipType.KAKAO;
import static io.github.cni274.membership.enums.MembershipType.NAVER;
import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    @DisplayName("나의 멤버십 전체 조회 성공 - 데이터 사이즈가 0인 경우")
    void successful_size0() {
        List<Membership> findMembershipList = membershipRepository.findAllByUserId("userId");

        assertThat(findMembershipList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("나의 멤버십 전체 조회 성공 - 데이터 사이즈가 2인 경우")
    void successful_size2() {
        // given
        Membership membership1 = Membership.builder()
                .userId("userId")
                .membershipType(NAVER)
                .point(10000)
                .build();

        Membership membership2 = Membership.builder()
                .userId("userId")
                .membershipType(KAKAO)
                .point(20000)
                .build();

        membershipRepository.save(membership1);
        membershipRepository.save(membership2);

        // when
        List<Membership> findMembershipList = membershipRepository.findAllByUserId("userId");

        // then
        assertThat(findMembershipList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("나의 멤버십 삭제 성공")
    void successfulDeleteMembership() {
        var naverMembership = Membership.builder()
                .userId("userId")
                .membershipType(NAVER)
                .point(10000)
                .build();

        Membership savedMembership = membershipRepository.save(naverMembership);

        membershipRepository.deleteById(savedMembership.getId());

        Optional<Membership> findMembership = membershipRepository.findById(savedMembership.getId());
        assertThat(findMembership.isEmpty()).isTrue();
    }
}