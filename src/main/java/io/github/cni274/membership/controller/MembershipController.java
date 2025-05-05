package io.github.cni274.membership.controller;

import io.github.cni274.membership.dto.MembershipAccumulateRequest;
import io.github.cni274.membership.dto.MembershipAddResponse;
import io.github.cni274.membership.dto.MembershipDetailResponse;
import io.github.cni274.membership.dto.MembershipRequest;
import io.github.cni274.membership.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.github.cni274.membership.constants.MembershipConstants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/api/v1/memberships")
    public ResponseEntity<MembershipAddResponse> addMembership(
            @RequestHeader(USER_ID_HEADER) String userId,
            @RequestBody @Valid MembershipRequest membershipRequest) {

        MembershipAddResponse membershipAddResponse = membershipService.addMembership(userId, membershipRequest.getMembershipType(), membershipRequest.getPoint());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipAddResponse);
    }

    @GetMapping("/api/v1/memberships")
    public ResponseEntity<List<MembershipDetailResponse>> getMembershipList(
            @RequestHeader(USER_ID_HEADER) String userId) {
        return ResponseEntity.ok(membershipService.getMembershipList(userId));
    }

    @GetMapping("/api/v1/memberships/{membershipId}")
    public ResponseEntity<MembershipDetailResponse> getMembershipDetail(
            @PathVariable Long membershipId,
            @RequestHeader(USER_ID_HEADER) String userId) {
        return ResponseEntity.ok(membershipService.getMembership(membershipId, userId));
    }

    @DeleteMapping("/api/v1/memberships/{membershipId}")
    public ResponseEntity<Void> deleteMembership(
            @PathVariable Long membershipId,
            @RequestHeader(USER_ID_HEADER) String userId) {

        membershipService.removeMembership(membershipId, userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/memberships/{membershipId}/accumulate")
    public ResponseEntity<Void> accumulateMembershipPoint(
            @PathVariable Long membershipId,
            @RequestHeader(USER_ID_HEADER) String userId,
            @RequestBody @Valid MembershipAccumulateRequest membershipRequest) {
        membershipService.accumulateMembershipPoint(membershipId, userId, membershipRequest.getPoint());
        return ResponseEntity.noContent().build();
    }
}
