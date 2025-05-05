package io.github.cni274.membership.dto;

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
public final class MembershipAccumulateRequest {
    @NotNull
    @Min(0)
    private final Integer point;
}
