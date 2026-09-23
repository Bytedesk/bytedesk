/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-21
 * @Description: T1/T2 联系方式镜像同步单测（docs/plans/2026-09-21-user-member-contact-sync-plan.md）
 */
package com.bytedesk.core.rbac.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.exception.MobileExistsException;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationRepository;
import com.bytedesk.core.rbac.role.RoleRestService;
import com.bytedesk.core.rbac.token.TokenRestService;
import com.bytedesk.core.uid.UidUtils;

class UserServiceMemberContactSyncTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final AuthService authService = mock(AuthService.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);

    private UserService createUserService() {
        return new UserService(
                userRepository,
                mock(ModelMapper.class),
                mock(RoleRestService.class),
                mock(BytedeskProperties.class),
                mock(BCryptPasswordEncoder.class),
                mock(UidUtils.class),
                mock(OrganizationRepository.class),
                mock(BytedeskEventPublisher.class),
                authService,
                mock(TokenRestService.class),
                memberRepository);
    }

    private MemberEntity member(String uid, String orgUid, UserEntity user, String mobile) {
        MemberEntity member = MemberEntity.builder()
                .uid(uid)
                .orgUid(orgUid)
                .mobile(mobile)
                .build();
        member.setUser(user);
        return member;
    }

    @Test
    void changeMobileShouldSyncAllActiveMembers() {
        UserService userService = createUserService();

        UserEntity user = UserEntity.builder()
                .id(1L)
                .uid("user-1")
                .mobile("13345678000")
                .country("86")
                .platform("BYTEDESK")
                .build();
        MemberEntity memberOrg1 = member("member-1", "org-1", user, "13345678000");
        MemberEntity memberOrg2 = member("member-2", "org-2", user, "13345678000");

        when(authService.getUser()).thenReturn(user);
        when(userRepository.findByUid("user-1")).thenReturn(Optional.of(user));
        when(userRepository.existsByMobileAndCountryAndPlatformAndDeletedFalse(
                anyString(), anyString(), anyString())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(memberRepository.findByUser_UidAndDeletedFalse("user-1"))
                .thenReturn(List.of(memberOrg1, memberOrg2));
        when(memberRepository.findByMobileAndCountryAndOrgUidAndDeletedFalse(
                anyString(), anyString(), anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmailAndOrgUidAndDeletedFalse(anyString(), anyString()))
                .thenReturn(Optional.empty());

        UserRequest request = UserRequest.builder()
                .mobile("13311156272")
                .country("86")
                .platform("BYTEDESK")
                .build();

        userService.changeMobile(request);

        assertEquals("13311156272", user.getMobile());
        assertEquals("13311156272", memberOrg1.getMobile());
        assertEquals("13311156272", memberOrg2.getMobile());
        verify(memberRepository).saveAll(List.of(memberOrg1, memberOrg2));
    }

    @Test
    void changeMobileShouldRollbackWhenNewMobileOccupiedByOtherMemberInSameOrg() {
        UserService userService = createUserService();

        UserEntity user = UserEntity.builder()
                .id(1L)
                .uid("user-1")
                .mobile("13345678000")
                .country("86")
                .platform("BYTEDESK")
                .build();
        MemberEntity ownMember = member("member-1", "org-1", user, "13345678000");

        // 同组织内另一个用户（user-other）的成员已占用新手机号
        UserEntity otherUser = UserEntity.builder().id(2L).uid("user-other").build();
        MemberEntity occupied = member("member-x", "org-1", otherUser, "13311156272");

        when(authService.getUser()).thenReturn(user);
        when(userRepository.findByUid("user-1")).thenReturn(Optional.of(user));
        when(userRepository.existsByMobileAndCountryAndPlatformAndDeletedFalse(
                anyString(), anyString(), anyString())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(memberRepository.findByUser_UidAndDeletedFalse("user-1")).thenReturn(List.of(ownMember));
        when(memberRepository.findByMobileAndCountryAndOrgUidAndDeletedFalse(
                "13311156272", "86", "org-1")).thenReturn(Optional.of(occupied));

        UserRequest request = UserRequest.builder()
                .mobile("13311156272")
                .country("86")
                .platform("BYTEDESK")
                .build();

        assertThrows(MobileExistsException.class, () -> userService.changeMobile(request));
        verify(memberRepository, never()).saveAll(any());
    }

    @Test
    void syncMemberContactsShouldSkipWhenNoActiveMembers() {
        UserService userService = createUserService();
        UserEntity user = UserEntity.builder().id(1L).uid("user-1").mobile("13311156272").build();

        when(memberRepository.findByUser_UidAndDeletedFalse("user-1")).thenReturn(List.of());

        userService.syncMemberContacts(user);

        verify(memberRepository, never()).saveAll(any());
    }

    @Test
    void syncUserContactsFromMemberShouldMirrorMobileToUserAndOtherMembers() {
        UserService userService = createUserService();

        UserEntity user = UserEntity.builder()
                .id(1L)
                .uid("user-1")
                .mobile("13345678000")
                .country("86")
                .platform("BYTEDESK")
                .build();
        // user-1 在另一个组织的成员，应被正向同步覆盖
        MemberEntity otherOrgMember = member("member-2", "org-2", user, "13345678000");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByMobileAndCountryAndPlatformAndDeletedFalse(
                anyString(), anyString(), anyString())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(memberRepository.findByUser_UidAndDeletedFalse("user-1")).thenReturn(List.of(otherOrgMember));
        when(memberRepository.findByMobileAndCountryAndOrgUidAndDeletedFalse(
                anyString(), anyString(), anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmailAndOrgUidAndDeletedFalse(anyString(), anyString()))
                .thenReturn(Optional.empty());

        UserEntity updated = userService.syncUserContactsFromMember(
                user, "13311156272", null, "86");

        assertEquals("13311156272", updated.getMobile());
        assertEquals("13311156272", otherOrgMember.getMobile());
        verify(memberRepository).saveAll(List.of(otherOrgMember));
    }

    @Test
    void syncUserContactsFromMemberShouldRejectMobileOwnedByOtherPlatformUser() {
        UserService userService = createUserService();

        UserEntity user = UserEntity.builder()
                .id(1L)
                .uid("user-1")
                .mobile("13345678000")
                .country("86")
                .platform("BYTEDESK")
                .build();
        UserEntity other = UserEntity.builder().id(2L).uid("user-other").mobile("13311156272").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByMobileAndCountryAndPlatformAndDeletedFalse(
                anyString(), anyString(), anyString())).thenReturn(true);
        when(userRepository.findByMobileAndCountryAndPlatformAndDeletedFalse(
                anyString(), anyString(), anyString())).thenReturn(Optional.of(other));

        assertThrows(MobileExistsException.class,
                () -> userService.syncUserContactsFromMember(user, "13311156272", null, "86"));
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(memberRepository, never()).saveAll(any());
    }

    @Test
    void syncUserContactsFromMemberShouldNotClearUserContactsWhenMemberValuesBlank() {
        UserService userService = createUserService();

        UserEntity user = UserEntity.builder()
                .id(1L)
                .uid("user-1")
                .mobile("13311156272")
                .email("a@email.com")
                .country("86")
                .platform("BYTEDESK")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserEntity updated = userService.syncUserContactsFromMember(user, null, "", "86");

        assertEquals("13311156272", updated.getMobile());
        assertEquals("a@email.com", updated.getEmail());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(memberRepository, never()).saveAll(any());
    }
}
