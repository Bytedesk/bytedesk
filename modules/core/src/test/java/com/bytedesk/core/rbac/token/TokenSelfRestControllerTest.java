package com.bytedesk.core.rbac.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class TokenSelfRestControllerTest {

    private static final String USER_UID = "user-1";
    private static final String ORG_UID = "org-1";
    private static final String CURRENT_ACCESS_TOKEN = "jwt-current";

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BytedeskProperties bytedeskProperties;

    @Mock
    private HttpServletRequest httpServletRequest;

    private TokenRestService tokenRestService;
    private TokenRestController controller;

    @BeforeEach
    void setUp() {
        tokenRestService = new TokenRestService(
                tokenRepository,
                modelMapper,
                uidUtils,
                authService,
                userRepository,
                bytedeskProperties);
        controller = new TokenRestController(tokenRestService);
    }

    private UserEntity currentUser() {
        UserEntity user = new UserEntity();
        user.setUid(USER_UID);
        com.bytedesk.core.rbac.organization.OrganizationEntity org =
                new com.bytedesk.core.rbac.organization.OrganizationEntity();
        org.setUid(ORG_UID);
        user.setCurrentOrganization(org);
        return user;
    }

    @SuppressWarnings("unchecked")
    @Test
    void querySelfSessionsReturnsPageData() {
        when(authService.getCurrentUser()).thenReturn(currentUser());
        TokenEntity current = TokenEntity.builder()
                .uid("token-current")
                .accessToken(CURRENT_ACCESS_TOKEN)
                .type(TokenTypeEnum.BEARER.name())
                .userUid(USER_UID)
                .orgUid(ORG_UID)
                .revoked(false)
                .deleted(false)
                .permanent(true)
                .createdAt(BdDateUtils.now().minusDays(1))
                .channel("FLUTTER")
                .device("iPhone 15 Pro · iOS 18")
                .build();
        when(tokenRepository.findValidSelfSessions(
                org.mockito.ArgumentMatchers.eq(USER_UID),
                org.mockito.ArgumentMatchers.eq(ORG_UID),
                org.mockito.ArgumentMatchers.eq("BEARER"),
                org.mockito.ArgumentMatchers.any(ZonedDateTime.class),
                org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(current)));
        when(tokenRepository.findByAccessTokenAndUserUidAndOrgUidAndDeletedFalse(
                CURRENT_ACCESS_TOKEN, USER_UID, ORG_UID))
                .thenReturn(List.of(current));
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + CURRENT_ACCESS_TOKEN);

        ResponseEntity<?> response = controller.querySelfSessions(0, 20, "FLUTTER", httpServletRequest);

        assertEquals(200, response.getStatusCode().value());
        com.bytedesk.core.utils.JsonResult<?> body =
                (com.bytedesk.core.utils.JsonResult<?>) response.getBody();
        assertEquals(200, body.getCode());
        TokenSelfPageResponse data = (TokenSelfPageResponse) body.getData();
        assertEquals(1, data.getTotalElements());
        assertEquals(Boolean.TRUE, data.getCurrentIncluded());
        assertEquals("iPhone 15 Pro · iOS 18", data.getContent().get(0).getDevice());
    }

    @Test
    void querySelfSessionsRejectsInvalidPagination() {
        ResponseEntity<?> negativePage = controller.querySelfSessions(-1, 20, null, httpServletRequest);
        assertEquals(400, negativePage.getStatusCode().value());

        ResponseEntity<?> oversizePage = controller.querySelfSessions(0, 101, null, httpServletRequest);
        assertEquals(400, oversizePage.getStatusCode().value());
    }

    @SuppressWarnings("unchecked")
    @Test
    void revokeSelfSessionReturnsSanitizedDetail() {
        when(authService.getCurrentUser()).thenReturn(currentUser());
        TokenEntity other = TokenEntity.builder()
                .uid("token-other")
                .accessToken("jwt-other")
                .type(TokenTypeEnum.BEARER.name())
                .userUid(USER_UID)
                .orgUid(ORG_UID)
                .revoked(false)
                .deleted(false)
                .permanent(true)
                .createdAt(BdDateUtils.now().minusDays(1))
                .channel("WEB")
                .device("Chrome on Windows 11")
                .build();
        when(tokenRepository.findByUidAndUserUidAndOrgUidAndDeletedFalse("token-other", USER_UID, ORG_UID))
                .thenReturn(java.util.Optional.of(other));
        when(tokenRepository.save(org.mockito.ArgumentMatchers.any(TokenEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + CURRENT_ACCESS_TOKEN);

        ResponseEntity<?> response = controller.revokeSelfSession(
                TokenSelfRevokeRequest.builder().uid("token-other").channel("FLUTTER").build(),
                httpServletRequest);

        assertEquals(200, response.getStatusCode().value());
        com.bytedesk.core.utils.JsonResult<?> body =
                (com.bytedesk.core.utils.JsonResult<?>) response.getBody();
        TokenSelfResponse data = (TokenSelfResponse) body.getData();
        assertEquals("REVOKED", data.getStatus());
        assertEquals(Boolean.FALSE, data.getIsCurrent());
    }

    @Test
    void revokeSelfSessionRejectsMissingUid() {
        ResponseEntity<?> response = controller.revokeSelfSession(
                TokenSelfRevokeRequest.builder().channel("FLUTTER").build(),
                httpServletRequest);

        assertEquals(400, response.getStatusCode().value());
    }
}
