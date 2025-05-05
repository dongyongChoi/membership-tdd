package io.github.cni274.membership.dto;

import io.github.cni274.membership.enums.MembershipType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public final class MembershipAddResponse {
    private final Long id;
    private final MembershipType membershipType;
}
