package com.bytedesk.core.black;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

@ExtendWith(MockitoExtension.class)
class BlackRestServiceTest {

    @Mock
    private BlackRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private AuthService authService;

    private BlackRestService blackRestService;

    @BeforeEach
    void setUp() {
        blackRestService = new BlackRestService(repository, modelMapper, uidUtils, authService);
    }

    @Test
    void existsByBlackUidReturnsFalseWhenOnlyOtherOrganizationHasBlacklist() {
        BlackRequest request = BlackRequest.builder()
                .blackUid("visitor-1")
                .build();
        request.setOrgUid("org-2");

        when(repository.findFirstByBlackUidAndLevelAndDeletedFalse("visitor-1", LevelEnum.PLATFORM.name()))
                .thenReturn(Optional.empty());
        when(repository.findFirstByBlackUidAndOrgUidAndLevelAndDeletedFalse(
                "visitor-1",
                "org-2",
                LevelEnum.ORGANIZATION.name()))
                .thenReturn(Optional.empty());

        assertThat(blackRestService.existsByBlackUid(request)).isFalse();
    }

    @Test
    void createAllowsSameVisitorUidInDifferentOrganizations() {
        BlackRequest request = BlackRequest.builder()
                .blackUid("visitor-1")
                .blackNickname("Visitor")
                .reason("org scoped")
                .build();
                request.setOrgUid("org-2");

        BlackEntity mapped = new BlackEntity();
        mapped.setBlackUid("visitor-1");
        mapped.setOrgUid("org-2");
        mapped.setReason("org scoped");

        BlackEntity saved = new BlackEntity();
        saved.setUid("black-1");
        saved.setBlackUid("visitor-1");
        saved.setOrgUid("org-2");
        saved.setLevel(LevelEnum.ORGANIZATION.name());

        BlackResponse response = BlackResponse.builder().uid("black-1").build();

        UserEntity user = new UserEntity();
        user.setUid("agent-1");
        user.setNickname("Agent");

        when(repository.findFirstByBlackUidAndLevelAndDeletedFalse("visitor-1", LevelEnum.PLATFORM.name()))
                .thenReturn(Optional.empty());
        when(repository.findFirstByBlackUidAndOrgUidAndLevelAndDeletedFalse(
                "visitor-1",
                "org-2",
                LevelEnum.ORGANIZATION.name()))
                .thenReturn(Optional.empty());
        when(authService.getUser()).thenReturn(user);
        when(modelMapper.map(request, BlackEntity.class)).thenReturn(mapped);
        when(uidUtils.getUid()).thenReturn("black-1");
        when(repository.save(any(BlackEntity.class))).thenReturn(saved);
        when(modelMapper.map(saved, BlackResponse.class)).thenReturn(response);

        BlackResponse created = blackRestService.create(request);

        assertThat(created.getUid()).isEqualTo("black-1");
        assertThat(mapped.getLevel()).isEqualTo(LevelEnum.ORGANIZATION.name());
    }
}