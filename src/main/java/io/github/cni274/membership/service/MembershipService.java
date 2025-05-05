package io.github.cni274.membership.service;

import io.github.cni274.membership.dto.MembershipAddResponse;
import io.github.cni274.membership.dto.MembershipDetailResponse;
import io.github.cni274.membership.entity.Membership;
import io.github.cni274.membership.enums.MembershipErrorResult;
import io.github.cni274.membership.enums.MembershipType;
import io.github.cni274.membership.exception.MembershipException;
import io.github.cni274.membership.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipAddResponse addMembership(String userId, MembershipType membershipType, Integer point) {
        Membership findMembership = membershipRepository.findByUserIdAndMembershipType(userId, membershipType);
        if (findMembership != null) {
            throw new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
        }

        Membership membership = Membership.builder()
                .userId(userId)
                .membershipType(membershipType)
                .point(point)
                .build();

        Membership savedMembership = membershipRepository.save(membership);

        return new MembershipAddResponse(
                savedMembership.getId(),
                savedMembership.getMembershipType()
        );

    }

    public List<MembershipDetailResponse> getMembershipList(String userId) {
        List<Membership> findMembershipList = membershipRepository.findAllByUserId(userId);

        return findMembershipList.stream()
                .map(MembershipDetailResponse::convert)
                .toList();
    }

    public MembershipDetailResponse getMembership(Long membershipId, String userId) {
        Optional<Membership> optionalMembership = membershipRepository.findById(membershipId);

        Membership membership = optionalMembership.orElseThrow(() -> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));

        if (!membership.getUserId().equals(userId)) {
            throw new MembershipException(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
        }

        return MembershipDetailResponse.convert(membership);
    }
}
