package com.bytedesk.ticket.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;

import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.message.MessageRepository;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.ticket.attachment.TicketAttachmentRepository;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;
import com.bytedesk.ticket.ticket_settings.TicketSettingsEntity;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRestService;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilityCategoryRuleData;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilityModeEnum;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilitySettingsData;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilitySettingsEntity;
import com.bytedesk.ticket.ticket_sla_record.TicketSlaRecordRepository;

class TicketRestServiceVisibilityTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void canViewTicketWhenCurrentDepartmentIsExplicitlyAllowedForCategory() {
        AuthService authService = mock(AuthService.class);
        TicketSettingsRestService ticketSettingsRestService = mock(TicketSettingsRestService.class);
        MemberRepository memberRepository = mock(MemberRepository.class);
        TicketRestService service = newService(authService, ticketSettingsRestService, memberRepository);

        UserEntity currentUser = buildUser("user-1");
        when(authService.getUser()).thenReturn(currentUser);
        when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-1", "org-1"))
                .thenReturn(Optional.of(buildMember(currentUser, "dept-b")));
        when(ticketSettingsRestService.findDefaultByOrgUidAndType("org-1", TicketTypeEnum.INTERNAL.name()))
                .thenReturn(Optional.of(buildSettings(TicketVisibilitySettingsData.builder()
                        .mode(TicketVisibilityModeEnum.CATEGORY_BASED.name())
                        .categoryRules(List.of(TicketVisibilityCategoryRuleData.builder()
                                .categoryUid("cat-1")
                                .visibility(TicketVisibilityModeEnum.DEPARTMENT_BASED.name())
                                .departmentUids(List.of("dept-b"))
                                .build()))
                        .build())));

        TicketEntity ticket = TicketEntity.builder()
                .uid("ticket-1")
                .orgUid("org-1")
                .type(TicketTypeEnum.INTERNAL.name())
                .categoryUid("cat-1")
                .departmentUid("dept-a")
                .userUid("reporter-1")
                .build();

        assertThat(invokeCanViewTicket(service, ticket)).isTrue();
    }

    @Test
    void canViewTicketWhenCurrentUserIsReporterEvenIfCategoryWouldOtherwiseBeRestricted() {
        AuthService authService = mock(AuthService.class);
        TicketRestService service = newService(authService, mock(TicketSettingsRestService.class), mock(MemberRepository.class));

        UserEntity currentUser = buildUser("reporter-1");
        when(authService.getUser()).thenReturn(currentUser);

        TicketEntity ticket = TicketEntity.builder()
                .uid("ticket-2")
                .orgUid("org-1")
                .type(TicketTypeEnum.INTERNAL.name())
                .categoryUid("cat-1")
                .departmentUid("dept-a")
                .userUid("reporter-1")
                .build();

        assertThat(invokeCanViewTicket(service, ticket)).isTrue();
    }

    @Test
    void canViewTicketWhenCurrentUserIsAssigneeEvenIfCategoryWouldOtherwiseBeRestricted() {
        AuthService authService = mock(AuthService.class);
        TicketRestService service = newService(authService, mock(TicketSettingsRestService.class), mock(MemberRepository.class));

        UserEntity currentUser = buildUser("user-2");
        when(authService.getUser()).thenReturn(currentUser);

        TicketEntity ticket = TicketEntity.builder()
                .uid("ticket-3")
                .orgUid("org-1")
                .type(TicketTypeEnum.INTERNAL.name())
                .categoryUid("cat-1")
                .departmentUid("dept-a")
                .userUid("reporter-1")
                .assignee("{\"uid\":\"user-2\"}")
                .build();

        assertThat(invokeCanViewTicket(service, ticket)).isTrue();
    }

    @Test
    void canViewTicketWithoutDepartmentWhenCategoryUsesDepartmentRestrictedMode() {
        AuthService authService = mock(AuthService.class);
        TicketSettingsRestService ticketSettingsRestService = mock(TicketSettingsRestService.class);
        MemberRepository memberRepository = mock(MemberRepository.class);
        TicketRestService service = newService(authService, ticketSettingsRestService, memberRepository);

        UserEntity currentUser = buildUser("user-3");
        when(authService.getUser()).thenReturn(currentUser);
        when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-3", "org-1"))
                .thenReturn(Optional.of(buildMember(currentUser, "dept-z")));
        when(ticketSettingsRestService.findDefaultByOrgUidAndType("org-1", TicketTypeEnum.INTERNAL.name()))
                .thenReturn(Optional.of(buildSettings(TicketVisibilitySettingsData.builder()
                        .mode(TicketVisibilityModeEnum.CATEGORY_BASED.name())
                        .categoryRules(List.of(TicketVisibilityCategoryRuleData.builder()
                                .categoryUid("cat-2")
                                .visibility(TicketVisibilityModeEnum.DEPARTMENT_RESTRICTED.name())
                                .build()))
                        .build())));

        TicketEntity ticket = TicketEntity.builder()
                .uid("ticket-4")
                .orgUid("org-1")
                .type(TicketTypeEnum.INTERNAL.name())
                .categoryUid("cat-2")
                .departmentUid(null)
                .userUid("reporter-2")
                .build();

        assertThat(invokeCanViewTicket(service, ticket)).isTrue();
    }

    @Test
    void canNotViewExternalTicketWithoutDepartmentWhenCategoryUsesDepartmentBasedAndCurrentDepartmentNotAllowed() {
        AuthService authService = mock(AuthService.class);
        TicketSettingsRestService ticketSettingsRestService = mock(TicketSettingsRestService.class);
        MemberRepository memberRepository = mock(MemberRepository.class);
        TicketRestService service = newService(authService, ticketSettingsRestService, memberRepository);

        UserEntity currentUser = buildUser("user-4");
        when(authService.getUser()).thenReturn(currentUser);
        when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-4", "org-1"))
                .thenReturn(Optional.of(buildMember(currentUser, "dept-z")));
        when(ticketSettingsRestService.findDefaultByOrgUidAndType("org-1", TicketTypeEnum.EXTERNAL.name()))
                .thenReturn(Optional.of(buildSettings(TicketTypeEnum.EXTERNAL, TicketVisibilitySettingsData.builder()
                        .mode(TicketVisibilityModeEnum.CATEGORY_BASED.name())
                        .categoryRules(List.of(TicketVisibilityCategoryRuleData.builder()
                                .categoryUid("cat-3")
                                .visibility(TicketVisibilityModeEnum.DEPARTMENT_BASED.name())
                                .departmentUids(List.of("dept-a"))
                                .build()))
                        .build())));

        TicketEntity ticket = TicketEntity.builder()
                .uid("ticket-5")
                .orgUid("org-1")
                .type(TicketTypeEnum.EXTERNAL.name())
                .categoryUid("cat-3")
                .departmentUid(null)
                .userUid("reporter-5")
                .build();

        assertThat(invokeCanViewTicket(service, ticket)).isFalse();
    }

    private static TicketRestService newService(AuthService authService,
            TicketSettingsRestService ticketSettingsRestService,
            MemberRepository memberRepository) {
        return new TicketRestService(
                mock(TicketRepository.class),
                mock(TicketAttachmentRepository.class),
                mock(ModelMapper.class),
                authService,
                mock(UidUtils.class),
                mock(ThreadRestService.class),
                mock(MessageRepository.class),
                mock(TicketSlaRecordRepository.class),
                mock(UploadRestService.class),
                mock(ApplicationEventPublisher.class),
                mock(CategoryRestService.class),
                ticketSettingsRestService,
                memberRepository);
    }

    private static UserEntity buildUser(String userUid) {
        return UserEntity.builder()
                .uid(userUid)
                .username(userUid)
                .build();
    }

    private static MemberEntity buildMember(UserEntity user, String deptUid) {
        return MemberEntity.builder()
                .uid("member-" + user.getUid())
                .orgUid("org-1")
                .user(user)
                .deptUid(deptUid)
                .build();
    }

        private static TicketSettingsEntity buildSettings(TicketTypeEnum type, TicketVisibilitySettingsData data) {
        return TicketSettingsEntity.builder()
                .uid("settings-1")
                .orgUid("org-1")
                                .type(type.name())
                .visibilitySettings(TicketVisibilitySettingsEntity.builder()
                        .uid("visibility-1")
                        .content(data)
                        .build())
                .build();
    }

        private static TicketSettingsEntity buildSettings(TicketVisibilitySettingsData data) {
                return buildSettings(TicketTypeEnum.INTERNAL, data);
        }

    private static boolean invokeCanViewTicket(TicketRestService service, TicketEntity ticket) {
        try {
            Method method = TicketRestService.class.getDeclaredMethod("canViewTicket", TicketEntity.class);
            method.setAccessible(true);
            return (boolean) method.invoke(service, ticket);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex.getTargetException());
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}