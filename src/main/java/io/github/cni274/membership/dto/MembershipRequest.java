package io.github.cni274.membership.dto;

import io.github.cni274.membership.enums.MembershipType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public final class MembershipRequest {
    @NotNull
    @Min(0)
    private final Integer point;

    @NotNull
    private final MembershipType membershipType;
}
