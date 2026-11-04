package com.bytedesk.core.rbac.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.exception.BusinessConflictException;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;

@ExtendWith(MockitoExtension.class)
class TokenSelfRestServiceTest {

    private static final String USER_UID = "user-1";
    private static final String ORG_UID = "org-1";
    private static final String CURRENT_ACCESS_TOKEN = "jwt-current";
    private static final String OTHER_ACCESS_TOKEN = "jwt-other";

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

    private TokenRestService tokenRestService;

    @BeforeEach
    void setUp() {
        tokenRestService = new TokenRestService(
                tokenRepository,
                modelMapper,
                uidUtils,
                authService,
                userRepository,
                bytedeskProperties);
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

    private TokenEntity token(String uid, String accessToken, boolean revoked, ZonedDateTime expiresAt,
            boolean permanent) {
        return TokenEntity.builder()
                .uid(uid)
                .accessToken(accessToken)
                .type(TokenTypeEnum.BEARER.name())
                .userUid(USER_UID)
                .orgUid(ORG_UID)
                .revoked(revoked)
                .deleted(false)
                .permanent(permanent)
                .expiresAt(expiresAt)
                .createdAt(BdDateUtils.now().minusDays(1))
                .channel("FLUTTER")
                .device("iPhone 15 Pro · iOS 18")
                .build();
    }

    @Test
    void querySelfSessionsReturnsPreciseTotalAndCurrentFlag() {
        when(authService.getCurrentUser()).thenReturn(currentUser());
        TokenEntity current = token("token-current", CURRENT_ACCESS_TOKEN, false, null, true);
        TokenEntity other = token("token-other", OTHER_ACCESS_TOKEN, false, null, true);
        when(tokenRepository.findValidSelfSessions(eq(USER_UID), eq(ORG_UID), eq("BEARER"),
                any(ZonedDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(current, other)));
        when(tokenRepository.findByAccessTokenAndUserUidAndOrgUidAndDeletedFalse(
                CURRENT_ACCESS_TOKEN, USER_UID, ORG_UID))
                .thenReturn(List.of(current));

        TokenSelfPageResponse page = tokenRestService.querySelfSessions(CURRENT_ACCESS_TOKEN, 0, 20);

        assertEquals(2, page.getTotalElements());
        assertTrue(page.getCurrentIncluded());
        assertEquals(Boolean.TRUE, page.getContent().get(0).getIsCurrent());
        assertEquals(Boolean.FALSE, page.getContent().get(1).getIsCurrent());
        assertEquals("VALID", page.getContent().get(0).getStatus());
    }

    @Test
    void homeCountWithPageSizeOneKeepsCurrentIncludedWhenCurrentNotInPage() {
        // 首页计数场景：pageSize=1 只返回最新一条（非本机），
        // currentIncluded 仍必须为 true（当前凭证在归属内唯一匹配），否则首页提示被错误隐藏
        when(authService.getCurrentUser()).thenReturn(currentUser());
        TokenEntity current = token("token-current", CURRENT_ACCESS_TOKEN, false, null, true);
        // current 创建时间更早：最新一条是 other
        TokenEntity other = token("token-other", OTHER_ACCESS_TOKEN, false, null, true);
        current.setCreatedAt(BdDateUtils.now().minusDays(5));
        when(tokenRepository.findValidSelfSessions(eq(USER_UID), eq(ORG_UID), eq("BEARER"),
                any(ZonedDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(other)));
        when(tokenRepository.findByAccessTokenAndUserUidAndOrgUidAndDeletedFalse(
                CURRENT_ACCESS_TOKEN, USER_UID, ORG_UID))
                .thenReturn(List.of(current));

        TokenSelfPageResponse page = tokenRestService.querySelfSessions(CURRENT_ACCESS_TOKEN, 0, 1);

        // 本页不含当前记录，但身份已确认
        assertTrue(page.getCurrentIncluded());
        assertEquals(1, page.getTotalElements());
        assertEquals(Boolean.FALSE, page.getContent().get(0).getIsCurrent());
    }

    @Test
    void querySelfSessionsThrowsConflictWhenCurrentTokenMatchesMultipleRecords() {
        when(authService.getCurrentUser()).thenReturn(currentUser());
        TokenEntity current = token("token-current", CURRENT_ACCESS_TOKEN, false, null, true);
        TokenEntity duplicate = token("token-duplicate", CURRENT_ACCESS_TOKEN, false, null, true);
        when(tokenRepository.findValidSelfSessions(eq(USER_UID), eq(ORG_UID), eq("BEARER"),
                any(ZonedDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(current)));
        when(tokenRepository.findByAccessTokenAndUserUidAndOrgUidAndDeletedFalse(
                CURRENT_ACCESS_TOKEN, USER_UID, ORG_UID))
                .thenReturn(List.of(current, duplicate));

        assertThrows(BusinessConflictException.class,
                () -> tokenRestService.querySelfSessions(CURRENT_ACCESS_TOKEN, 0, 20));
    }

    @Test
    void querySelfSessionsThrowsWhenUserHasNoOrganization() {
        UserEntity noOrgUser = new UserEntity();
        noOrgUser.setUid(USER_UID);
        when(authService.getCurrentUser()).thenReturn(noOrgUser);

        assertThrows(BusinessConflictException.class,
                () -> tokenRestService.querySelfSessions(CURRENT_ACCESS_TOKEN, 0, 20));
    }

    @Test
    void querySelfByUidAllowsRevokedRecordAndMarksCurrent() {
        when(authService.getCurrentUser()).thenReturn(currentUser());
        TokenEntity revoked = token("token-revoked", OTHER_ACCESS_TOKEN, true, null, true);
        revoked.setRevokeReason("revoked by self");
        when(tokenRepository.findByUidAndUserUidAndOrgUidAndDeletedFalse("token-revoked", USER_UID, ORG_UID))
                .thenReturn(Optional.of(revoked));

        TokenSelfResponse response = tokenRestService.querySelfSessionByUid(CURRENT_ACCESS_TOKEN, "token-revoked");

        assertEquals("REVOKED", response.getStatus());
        assertEquals(Boolean.FALSE, response.getIsCurrent());
        assertEquals("revoked by self", response.getRevokeReason());
    }

    @Test
    void querySelfByUidHidesOtherUsersAndNonBearerTokens() {
        when(authService.getCurrentUser()).thenReturn(currentUser());
        when(tokenRepository.findByUidAndUserUidAndOrgUidAndDeletedFalse("other-token", USER_UID, ORG_UID))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> tokenRestService.querySelfSessionByUid(CURRENT_ACCESS_TOKEN, "other-token"));

        TokenEntity apiKey = token("api-token", OTHER_ACCESS_TOKEN, false, null, true);
        apiKey.setType("API");
        when(tokenRepository.findByUidAndUserUidAndOrgUidAndDeletedFalse("api-token", USER_UID, ORG_UID))
                .thenReturn(Optional.of(apiKey));
        assertThrows(NotFoundException.class,
                () -> tokenRestService.querySelfSessionByUid(CURRENT_ACCESS_TOKEN, "api-token"));
    }

    @Test
    void revokeSelfSessionRevokesOtherValidSession() {
        when(authService.getCurrentUser()).thenReturn(currentUser());
        TokenEntity other = token("token-other", OTHER_ACCESS_TOKEN, false, null, true);
        when(tokenRepository.findByUidAndUserUidAndOrgUidAndDeletedFalse("token-other", USER_UID, ORG_UID))
                .thenReturn(Optional.of(other));
        lenient().when(tokenRepository.save(any(TokenEntity.class))).thenReturn(other);

        TokenSelfResponse response = tokenRestService.revokeSelfSession(
                CURRENT_ACCESS_TOKEN, "token-other", "FLUTTER");

        assertEquals("REVOKED", response.getStatus());
        assertTrue(Boolean.TRUE.equals(other.getRevoked()));
        assertTrue(other.getRevokeReason().contains("revoked by self"));
        verify(tokenRepository).save(other);
    }

    @Test
    void revokeSelfSessionRejectsCurrentSession() {
        when(authService.getCurrentUser()).thenReturn(currentUser());
        TokenEntity current = token("token-current", CURRENT_ACCESS_TOKEN, false, null, true);
        when(tokenRepository.findByUidAndUserUidAndOrgUidAndDeletedFalse("token-current", USER_UID, ORG_UID))
                .thenReturn(Optional.of(current));

        assertThrows(BusinessConflictException.class,
                () -> tokenRestService.revokeSelfSession(CURRENT_ACCESS_TOKEN, "token-current", "FLUTTER"));
        verify(tokenRepository, never()).save(any(TokenEntity.class));
    }

    @Test
    void revokeSelfSessionIsIdempotentForAlreadyRevokedOtherSession() {
        when(authService.getCurrentUser()).thenReturn(currentUser());
        TokenEntity revoked = token("token-revoked", OTHER_ACCESS_TOKEN, true, null, true);
        when(tokenRepository.findByUidAndUserUidAndOrgUidAndDeletedFalse("token-revoked", USER_UID, ORG_UID))
                .thenReturn(Optional.of(revoked));

        TokenSelfResponse response = tokenRestService.revokeSelfSession(
                CURRENT_ACCESS_TOKEN, "token-revoked", "FLUTTER");

        assertEquals("REVOKED", response.getStatus());
        verify(tokenRepository, never()).save(any(TokenEntity.class));
    }

    @Test
    void revokeSelfSessionRejectsExpiredSession() {
        when(authService.getCurrentUser()).thenReturn(currentUser());
        TokenEntity expired = token("token-expired", OTHER_ACCESS_TOKEN, false,
                BdDateUtils.now().minusHours(1), false);
        when(tokenRepository.findByUidAndUserUidAndOrgUidAndDeletedFalse("token-expired", USER_UID, ORG_UID))
                .thenReturn(Optional.of(expired));

        assertThrows(BusinessConflictException.class,
                () -> tokenRestService.revokeSelfSession(CURRENT_ACCESS_TOKEN, "token-expired", "FLUTTER"));
        verify(tokenRepository, never()).save(any(TokenEntity.class));
    }

    @Test
    void revokeSelfSessionRejectsForeignUidWithNotFound() {
        when(authService.getCurrentUser()).thenReturn(currentUser());
        when(tokenRepository.findByUidAndUserUidAndOrgUidAndDeletedFalse(anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> tokenRestService.revokeSelfSession(CURRENT_ACCESS_TOKEN, "foreign", "FLUTTER"));
    }
}
