package io.github.cni274.membership.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cni274.membership.advice.GlobalExceptionHandler;
import io.github.cni274.membership.dto.MembershipAddResponse;
import io.github.cni274.membership.dto.MembershipDetailResponse;
import io.github.cni274.membership.dto.MembershipRequest;
import io.github.cni274.membership.enums.MembershipErrorResult;
import io.github.cni274.membership.enums.MembershipType;
import io.github.cni274.membership.exception.MembershipException;
import io.github.cni274.membership.service.MembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static io.github.cni274.membership.constants.MembershipConstants.USER_ID_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MembershipControllerTest {

    @InjectMocks
    MembershipController target;

    @Mock
    MembershipService membershipService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    private String url = "/api/v1/memberships";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("멤버십 등록 실패 - 사용자 식별값이 헤더에 없음")
    void failedToAddMembership_NoHeaderValue() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(membershipRequest(10000, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("멤버십 등록 실패 - MemberService에서 예외 발생")
    void failedToAddMembership_ExceptionsInMemberService() throws Exception {
        doThrow(new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER))
                .when(membershipService)
                .addMembership("12345", MembershipType.NAVER, 10000);

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(objectMapper.writeValueAsString(membershipRequest(10000, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isBadRequest());

        verify(membershipService).addMembership("12345", MembershipType.NAVER, 10000);
    }

    @Test
    @DisplayName("멤버십 등록 성공")
    void successfulAddMembership() throws Exception {
        MembershipAddResponse membershipAddResponse = new MembershipAddResponse(-1L, MembershipType.NAVER);
        doReturn(membershipAddResponse).when(membershipService).addMembership("12345", MembershipType.NAVER, 10000);

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(objectMapper.writeValueAsString(membershipRequest(10000, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isCreated());

        MembershipAddResponse response = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), MembershipAddResponse.class);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getMembershipType()).isEqualTo(MembershipType.NAVER);
    }

    @ParameterizedTest
    @MethodSource("invalidMembershipAddParameter")
    @DisplayName("멤버십 등록 실패 - 잘못된 파라미터 입력")
    void failedToAddMembership_wrongParameter(Integer point, MembershipType membershipType) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(objectMapper.writeValueAsString(membershipRequest(point, membershipType)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("멤버십 조회 실패 - 사용자 식별값이 헤더에 없음")
    void failedGetMembership_NoHeaderValue() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("멤버십 조회 성공")
    void successfulGetMemberList() throws Exception {
        doReturn(List.of(
                membershipDetailResponse(-1L, MembershipType.NAVER, 10001, LocalDateTime.now()),
                membershipDetailResponse(-2L, MembershipType.KAKAO, 10002, LocalDateTime.now()),
                membershipDetailResponse(-3L, MembershipType.LINE, 10003, LocalDateTime.now())
        ))
                .when(membershipService)
                .getMembershipList("12345");

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header(USER_ID_HEADER, "12345")
        );

        resultActions.andExpect(status().isOk());

        verify(membershipService).getMembershipList("12345");
    }

    @Test
    @DisplayName("멤버십 상세 조회 실패 - 사용자 식별갑이 헤더에 없음")
    void failedGetMembershipDetail_NoHeaderValue() throws Exception {
        String url = "/api/v1/memberships/-1";

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("멤버십 상세 조회 실패 - 멤버십 존재하지 않음")
    void failedGetMembershipDetail_MembershipNotFound() throws Exception {
        String url = "/api/v1/memberships/-1";

        doThrow(new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND))
                .when(membershipService)
                .getMembership(-1L, "12345");

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header(USER_ID_HEADER, "12345")
        );

        resultActions.andExpect(status().isNotFound());

        verify(membershipService).getMembership(-1L, "12345");
    }

    @Test
    @DisplayName("멤버십 상세 조회 성공")
    void successfulGetMembershipDetail() throws Exception {
        String url = "/api/v1/memberships/-1";

        doReturn(membershipDetailResponse(-1, MembershipType.NAVER, 10000, LocalDateTime.now()))
                .when(membershipService)
                .getMembership(-1L, "12345");

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header(USER_ID_HEADER, "12345")
        );

        resultActions.andExpect(status().isOk());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        MembershipDetailResponse membershipDetailResponse = objectMapper
                .readValue(contentAsString, MembershipDetailResponse.class);

        assertThat(membershipDetailResponse.getId()).isNotNull();
        assertThat(membershipDetailResponse.getId()).isEqualTo(-1L);
        assertThat(membershipDetailResponse.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(membershipDetailResponse.getPoint()).isEqualTo(10000);

        verify(membershipService).getMembership(-1L, "12345");

    }

    @Test
    @DisplayName("멤버십 삭제 실패 - 사용자 식별갑이 헤더에 없음")
    void failedRemoveMembership_NoHeaderValue() throws Exception {
        String url = "/api/v1/memberships/-1";
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
        );

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("멤버십 삭제 성공")
    void successfulRemoveMembership() throws Exception {
        String url = "/api/v1/memberships/-1";

        doNothing().when(membershipService).removeMembership(-1L, "userId");

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
                        .header(USER_ID_HEADER, "userId")
        );

        resultActions.andExpect(status().isNoContent());

        verify(membershipService).removeMembership(-1L, "userId");
    }


    private MembershipDetailResponse membershipDetailResponse(long id, MembershipType membershipType, int point, LocalDateTime now) {
        return MembershipDetailResponse.builder()
                .id(id)
                .membershipType(membershipType)
                .point(point)
                .createdAt(now)
                .build();
    }

    private static Stream<Arguments> invalidMembershipAddParameter() {
        return Stream.of(
                Arguments.of(null, MembershipType.NAVER),
                Arguments.of(-1, MembershipType.NAVER),
                Arguments.of(10000, null)
        );
    }

    private MembershipRequest membershipRequest(Integer point, MembershipType membershipType) {
        return new MembershipRequest(point, membershipType);
    }
}