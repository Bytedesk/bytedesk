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
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
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
        void canViewExternalTicketWhenCurrentDepartmentIsAllowedByTopLevelDepartmentBased() {
                AuthService authService = mock(AuthService.class);
                TicketSettingsRestService ticketSettingsRestService = mock(TicketSettingsRestService.class);
                MemberRepository memberRepository = mock(MemberRepository.class);
                TicketRestService service = newService(authService, ticketSettingsRestService, memberRepository);

                UserEntity currentUser = buildUser("user-top-1");
                when(authService.getUser()).thenReturn(currentUser);
                when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-top-1", "org-1"))
                                .thenReturn(Optional.of(buildMember(currentUser, "dept-kefu")));
                when(ticketSettingsRestService.findDefaultByOrgUidAndType("org-1", TicketTypeEnum.EXTERNAL.name()))
                                .thenReturn(Optional.of(buildSettings(TicketTypeEnum.EXTERNAL,
                                                TicketVisibilitySettingsData.builder()
                                                                .mode(TicketVisibilityModeEnum.DEPARTMENT_BASED.name())
                                                                .departmentUids(List.of("dept-kefu"))
                                                                .build())));

                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-top-1")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .departmentUid("dept-other")
                                .userUid("reporter-top-1")
                                .build();

                assertThat(invokeCanViewTicket(service, ticket)).isTrue();
        }

        @Test
        void canNotViewExternalTicketWhenCurrentDepartmentIsNotAllowedByTopLevelDepartmentBased() {
                AuthService authService = mock(AuthService.class);
                TicketSettingsRestService ticketSettingsRestService = mock(TicketSettingsRestService.class);
                MemberRepository memberRepository = mock(MemberRepository.class);
                TicketRestService service = newService(authService, ticketSettingsRestService, memberRepository);

                UserEntity currentUser = buildUser("user-top-2");
                when(authService.getUser()).thenReturn(currentUser);
                when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-top-2", "org-1"))
                                .thenReturn(Optional.of(buildMember(currentUser, "dept-design")));
                when(ticketSettingsRestService.findDefaultByOrgUidAndType("org-1", TicketTypeEnum.EXTERNAL.name()))
                                .thenReturn(Optional.of(buildSettings(TicketTypeEnum.EXTERNAL,
                                                TicketVisibilitySettingsData.builder()
                                                                .mode(TicketVisibilityModeEnum.DEPARTMENT_BASED.name())
                                                                .departmentUids(List.of("dept-kefu"))
                                                                .build())));

                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-top-2")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .departmentUid(null)
                                .categoryUid(null)
                                .userUid("reporter-top-2")
                                .build();

                assertThat(invokeCanViewTicket(service, ticket)).isFalse();
        }

        @Test
        void canViewExternalTicketWhenCurrentUserIsAssigneeEvenIfTopLevelDepartmentBasedWouldRestrict() {
                AuthService authService = mock(AuthService.class);
                TicketRestService service = newService(authService, mock(TicketSettingsRestService.class),
                                mock(MemberRepository.class));

                UserEntity currentUser = buildUser("user-top-3");
                when(authService.getUser()).thenReturn(currentUser);

                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-top-3")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .userUid("reporter-top-3")
                                .assignee("{\"uid\":\"user-top-3\"}")
                                .build();

                assertThat(invokeCanViewTicket(service, ticket)).isTrue();
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
                                                                .visibility(TicketVisibilityModeEnum.DEPARTMENT_BASED
                                                                                .name())
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
                TicketRestService service = newService(authService, mock(TicketSettingsRestService.class),
                                mock(MemberRepository.class));

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
                TicketRestService service = newService(authService, mock(TicketSettingsRestService.class),
                                mock(MemberRepository.class));

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
                                                                .visibility(TicketVisibilityModeEnum.DEPARTMENT_RESTRICTED
                                                                                .name())
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
                                .thenReturn(Optional.of(buildSettings(TicketTypeEnum.EXTERNAL,
                                                TicketVisibilitySettingsData.builder()
                                                                .mode(TicketVisibilityModeEnum.CATEGORY_BASED.name())
                                                                .categoryRules(List.of(TicketVisibilityCategoryRuleData
                                                                                .builder()
                                                                                .categoryUid("cat-3")
                                                                                .visibility(TicketVisibilityModeEnum.DEPARTMENT_BASED
                                                                                                .name())
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

        @Test
        void canViewExternalTicketWhenCurrentDepartmentIsExplicitlyAllowedForCategory() {
                AuthService authService = mock(AuthService.class);
                TicketSettingsRestService ticketSettingsRestService = mock(TicketSettingsRestService.class);
                MemberRepository memberRepository = mock(MemberRepository.class);
                TicketRestService service = newService(authService, ticketSettingsRestService, memberRepository);

                UserEntity currentUser = buildUser("user-5");
                when(authService.getUser()).thenReturn(currentUser);
                when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-5", "org-1"))
                                .thenReturn(Optional.of(buildMember(currentUser, "dept-kefu")));
                when(ticketSettingsRestService.findDefaultByOrgUidAndType("org-1", TicketTypeEnum.EXTERNAL.name()))
                                .thenReturn(Optional.of(buildSettings(TicketTypeEnum.EXTERNAL,
                                                TicketVisibilitySettingsData.builder()
                                                                .mode(TicketVisibilityModeEnum.CATEGORY_BASED.name())
                                                                .categoryRules(List.of(TicketVisibilityCategoryRuleData
                                                                                .builder()
                                                                                .categoryUid("cat-support")
                                                                                .visibility(TicketVisibilityModeEnum.DEPARTMENT_BASED
                                                                                                .name())
                                                                                .departmentUids(List.of("dept-kefu"))
                                                                                .build()))
                                                                .build())));

                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-6")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .categoryUid("cat-support")
                                .departmentUid("dept-other")
                                .userUid("reporter-6")
                                .build();

                assertThat(invokeCanViewTicket(service, ticket)).isTrue();
        }

        @Test
        void canNotViewExternalTicketWhenCurrentMemberHasNoDepartmentAndCategoryUsesDepartmentBased() {
                AuthService authService = mock(AuthService.class);
                TicketSettingsRestService ticketSettingsRestService = mock(TicketSettingsRestService.class);
                MemberRepository memberRepository = mock(MemberRepository.class);
                TicketRestService service = newService(authService, ticketSettingsRestService, memberRepository);

                // viewer 未分配部门：不在任何「选择部门」白名单内，应不可见
                UserEntity currentUser = buildUser("user-6");
                when(authService.getUser()).thenReturn(currentUser);
                when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-6", "org-1"))
                                .thenReturn(Optional.of(buildMember(currentUser, null)));
                when(ticketSettingsRestService.findDefaultByOrgUidAndType("org-1", TicketTypeEnum.EXTERNAL.name()))
                                .thenReturn(Optional.of(buildSettings(TicketTypeEnum.EXTERNAL,
                                                TicketVisibilitySettingsData.builder()
                                                                .mode(TicketVisibilityModeEnum.CATEGORY_BASED.name())
                                                                .categoryRules(List.of(TicketVisibilityCategoryRuleData
                                                                                .builder()
                                                                                .categoryUid("cat-support")
                                                                                .visibility(TicketVisibilityModeEnum.DEPARTMENT_BASED
                                                                                                .name())
                                                                                .departmentUids(List.of("dept-kefu"))
                                                                                .build()))
                                                                .build())));

                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-7")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .categoryUid("cat-support")
                                .departmentUid("dept-other")
                                .userUid("reporter-7")
                                .build();

                assertThat(invokeCanViewTicket(service, ticket)).isFalse();
        }

        @Test
        void canViewExternalTicketWhenCurrentUserIsReporterEvenIfCategoryWouldOtherwiseBeRestricted() {
                AuthService authService = mock(AuthService.class);
                TicketRestService service = newService(authService, mock(TicketSettingsRestService.class),
                                mock(MemberRepository.class));

                UserEntity currentUser = buildUser("reporter-8");
                when(authService.getUser()).thenReturn(currentUser);

                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-8")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .categoryUid("cat-support")
                                .departmentUid("dept-other")
                                .userUid("reporter-8")
                                .build();

                assertThat(invokeCanViewTicket(service, ticket)).isTrue();
        }

        @Test
        void canViewExternalTicketWhenCurrentUserIsAssigneeEvenIfCategoryWouldOtherwiseBeRestricted() {
                AuthService authService = mock(AuthService.class);
                TicketRestService service = newService(authService, mock(TicketSettingsRestService.class),
                                mock(MemberRepository.class));

                UserEntity currentUser = buildUser("user-7");
                when(authService.getUser()).thenReturn(currentUser);

                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-9")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .categoryUid("cat-support")
                                .departmentUid("dept-other")
                                .userUid("reporter-9")
                                .assignee("{\"uid\":\"user-7\"}")
                                .build();

                assertThat(invokeCanViewTicket(service, ticket)).isTrue();
        }

        @Test
        void populateUpdateDefaultsPreservesPersistedVisitorReporter() {
                TicketRestService service = newService(mock(AuthService.class), mock(TicketSettingsRestService.class),
                                mock(MemberRepository.class));
                TicketRequest request = TicketRequest.builder()
                                .reporter(UserProtobuf.builder().uid("admin-1").type(UserTypeEnum.USER.name()).build())
                                .build();
                request.setType(TicketTypeEnum.EXTERNAL.name());
                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-external-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .reporter(UserProtobuf.builder().uid("visitor-1").type(UserTypeEnum.VISITOR.name())
                                                .build().toJson())
                                .build();

                invokePopulateUpdateDefaults(service, request, ticket);

                assertThat(request.getReporter().getUid()).isEqualTo("visitor-1");
                assertThat(request.getReporter().getType()).isEqualTo(UserTypeEnum.VISITOR.name());
        }

        @Test
        void canNotViewUncategorizedExternalTicketWhenCategoryBasedRestrictingRulesExist() {
                AuthService authService = mock(AuthService.class);
                TicketSettingsRestService ticketSettingsRestService = mock(TicketSettingsRestService.class);
                MemberRepository memberRepository = mock(MemberRepository.class);
                TicketRestService service = newService(authService, ticketSettingsRestService, memberRepository);

                UserEntity currentUser = buildUser("user-8");
                when(authService.getUser()).thenReturn(currentUser);
                when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-8", "org-1"))
                                .thenReturn(Optional.of(buildMember(currentUser, "dept-design")));
                when(ticketSettingsRestService.findDefaultByOrgUidAndType("org-1", TicketTypeEnum.EXTERNAL.name()))
                                .thenReturn(Optional.of(buildSettings(TicketTypeEnum.EXTERNAL,
                                                TicketVisibilitySettingsData.builder()
                                                                .mode(TicketVisibilityModeEnum.CATEGORY_BASED.name())
                                                                .categoryRules(List.of(TicketVisibilityCategoryRuleData
                                                                                .builder()
                                                                                .categoryUid("cat-support")
                                                                                .visibility(TicketVisibilityModeEnum.DEPARTMENT_BASED
                                                                                                .name())
                                                                                .departmentUids(List.of("dept-kefu"))
                                                                                .build()))
                                                                .build())));

                // 无分类工单（访客提交/自动创建）：设计部成员不可见
                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-10")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .categoryUid(null)
                                .userUid("reporter-10")
                                .build();

                assertThat(invokeCanViewTicket(service, ticket)).isFalse();
        }

        @Test
        void canViewUncategorizedExternalTicketWhenNoRestrictingRulesConfigured() {
                AuthService authService = mock(AuthService.class);
                TicketSettingsRestService ticketSettingsRestService = mock(TicketSettingsRestService.class);
                MemberRepository memberRepository = mock(MemberRepository.class);
                TicketRestService service = newService(authService, ticketSettingsRestService, memberRepository);

                UserEntity currentUser = buildUser("user-9");
                when(authService.getUser()).thenReturn(currentUser);
                when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-9", "org-1"))
                                .thenReturn(Optional.of(buildMember(currentUser, "dept-design")));
                // 规则已全部退化为 ORG_WIDE（等价于无受限规则）：无分类工单保持可见，兼容既有部署
                when(ticketSettingsRestService.findDefaultByOrgUidAndType("org-1", TicketTypeEnum.EXTERNAL.name()))
                                .thenReturn(Optional.of(buildSettings(TicketTypeEnum.EXTERNAL,
                                                TicketVisibilitySettingsData.builder()
                                                                .mode(TicketVisibilityModeEnum.CATEGORY_BASED.name())
                                                                .categoryRules(List.of(TicketVisibilityCategoryRuleData
                                                                                .builder()
                                                                                .categoryUid("cat-any")
                                                                                .visibility(TicketVisibilityModeEnum.ORG_WIDE
                                                                                                .name())
                                                                                .build()))
                                                                .build())));

                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-11")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .categoryUid(null)
                                .userUid("reporter-11")
                                .build();

                assertThat(invokeCanViewTicket(service, ticket)).isTrue();
        }

        @Test
        void canNotViewTicketForOrgAdminOnNonAdminChannelWhenCategoryRestricted() {
                // desktop 等客服工作台渠道：组织管理员无特权，同样按可见性设置过滤
                AuthService authService = mock(AuthService.class);
                TicketSettingsRestService ticketSettingsRestService = mock(TicketSettingsRestService.class);
                MemberRepository memberRepository = mock(MemberRepository.class);
                TicketRestService service = newService(authService, ticketSettingsRestService, memberRepository);

                UserEntity currentUser = buildUser("user-admin-1");
                currentUser.setSuperUser(Boolean.FALSE);
                // 组织管理员角色
                com.bytedesk.core.rbac.role.RoleEntity adminRole = com.bytedesk.core.rbac.role.RoleEntity.builder()
                                .uid("role-admin")
                                .name("ROLE_ADMIN")
                                .build();
                currentUser.setCurrentRoles(new java.util.LinkedHashSet<>(java.util.List.of(adminRole)));
                when(authService.getUser()).thenReturn(currentUser);
                when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-admin-1", "org-1"))
                                .thenReturn(Optional.of(buildMember(currentUser, "dept-design")));
                when(ticketSettingsRestService.findDefaultByOrgUidAndType("org-1", TicketTypeEnum.EXTERNAL.name()))
                                .thenReturn(Optional.of(buildSettings(TicketTypeEnum.EXTERNAL,
                                                TicketVisibilitySettingsData.builder()
                                                                .mode(TicketVisibilityModeEnum.CATEGORY_BASED.name())
                                                                .categoryRules(List.of(TicketVisibilityCategoryRuleData
                                                                                .builder()
                                                                                .categoryUid("cat-support")
                                                                                .visibility(TicketVisibilityModeEnum.DEPARTMENT_BASED
                                                                                                .name())
                                                                                .departmentUids(List.of("dept-kefu"))
                                                                                .build()))
                                                                .build())));

                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-12")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .categoryUid("cat-support")
                                .departmentUid("dept-other")
                                .userUid("reporter-12")
                                .build();

                assertThat(invokeCanViewTicket(service, ticket, false)).isFalse();
        }

        @Test
        void canViewTicketForOrgAdminOnAdminChannel() {
                // admin 管理后台渠道（channel=WEB_ADMIN）：组织管理员保留全见特权
                AuthService authService = mock(AuthService.class);
                TicketRestService service = newService(authService, mock(TicketSettingsRestService.class),
                                mock(MemberRepository.class));

                UserEntity currentUser = buildUser("user-admin-2");
                com.bytedesk.core.rbac.role.RoleEntity adminRole = com.bytedesk.core.rbac.role.RoleEntity.builder()
                                .uid("role-admin")
                                .name("ROLE_ADMIN")
                                .build();
                currentUser.setCurrentRoles(new java.util.LinkedHashSet<>(java.util.List.of(adminRole)));
                when(authService.getUser()).thenReturn(currentUser);

                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-13")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .categoryUid("cat-support")
                                .departmentUid("dept-other")
                                .userUid("reporter-13")
                                .build();

                assertThat(invokeCanViewTicket(service, ticket, true)).isTrue();
        }

        @Test
        void canNotViewTicketForSuperUserOnNonAdminChannelWhenCategoryRestricted() {
                // desktop 渠道：超级管理员同样受可见性过滤
                AuthService authService = mock(AuthService.class);
                TicketSettingsRestService ticketSettingsRestService = mock(TicketSettingsRestService.class);
                MemberRepository memberRepository = mock(MemberRepository.class);
                TicketRestService service = newService(authService, ticketSettingsRestService, memberRepository);

                UserEntity currentUser = buildUser("user-super-1");
                currentUser.setSuperUser(Boolean.TRUE);
                when(authService.getUser()).thenReturn(currentUser);
                when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-super-1", "org-1"))
                                .thenReturn(Optional.of(buildMember(currentUser, "dept-design")));
                when(ticketSettingsRestService.findDefaultByOrgUidAndType("org-1", TicketTypeEnum.EXTERNAL.name()))
                                .thenReturn(Optional.of(buildSettings(TicketTypeEnum.EXTERNAL,
                                                TicketVisibilitySettingsData.builder()
                                                                .mode(TicketVisibilityModeEnum.CATEGORY_BASED.name())
                                                                .categoryRules(List.of(TicketVisibilityCategoryRuleData
                                                                                .builder()
                                                                                .categoryUid("cat-support")
                                                                                .visibility(TicketVisibilityModeEnum.DEPARTMENT_BASED
                                                                                                .name())
                                                                                .departmentUids(List.of("dept-kefu"))
                                                                                .build()))
                                                                .build())));

                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-14")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .categoryUid("cat-support")
                                .departmentUid("dept-other")
                                .userUid("reporter-14")
                                .build();

                assertThat(invokeCanViewTicket(service, ticket, false)).isFalse();
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
                return invokeCanViewTicket(service, ticket, true);
        }

        private static boolean invokeCanViewTicket(TicketRestService service, TicketEntity ticket,
                        boolean adminPrivilege) {
                try {
                        Method method = TicketRestService.class.getDeclaredMethod("canViewTicket", TicketEntity.class,
                                        boolean.class);
                        method.setAccessible(true);
                        return (boolean) method.invoke(service, ticket, adminPrivilege);
                } catch (InvocationTargetException ex) {
                        throw new RuntimeException(ex.getTargetException());
                } catch (ReflectiveOperationException ex) {
                        throw new RuntimeException(ex);
                }
        }

        private static void invokePopulateUpdateDefaults(TicketRestService service, TicketRequest request,
                        TicketEntity ticket) {
                try {
                        Method method = TicketRestService.class.getDeclaredMethod("populateUpdateDefaults",
                                        TicketRequest.class,
                                        TicketEntity.class);
                        method.setAccessible(true);
                        method.invoke(service, request, ticket);
                } catch (InvocationTargetException ex) {
                        throw new RuntimeException(ex.getTargetException());
                } catch (ReflectiveOperationException ex) {
                        throw new RuntimeException(ex);
                }
        }
}