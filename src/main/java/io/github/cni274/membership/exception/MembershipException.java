package io.github.cni274.membership.exception;

import io.github.cni274.membership.enums.MembershipErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MembershipException extends RuntimeException {
    private final MembershipErrorResult errorResult;
}
