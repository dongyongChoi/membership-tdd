package io.github.cni274.membership.repository;

import io.github.cni274.membership.entity.Membership;
import io.github.cni274.membership.enums.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Membership findByUserIdAndMembershipType(String userId, MembershipType membershipType);
}
