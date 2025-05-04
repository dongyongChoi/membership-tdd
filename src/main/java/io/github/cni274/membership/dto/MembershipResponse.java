package io.github.cni274.membership.dto;

import io.github.cni274.membership.enums.MembershipType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;


@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public final class MembershipResponse {
    private final Long id;
    private final MembershipType membershipType;
}
