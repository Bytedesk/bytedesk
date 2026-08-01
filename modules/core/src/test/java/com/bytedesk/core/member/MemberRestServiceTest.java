package com.bytedesk.core.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.bytedesk.core.exception.OrgMaxMembersExceededException;
import com.bytedesk.core.message.MessageService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRestService;
import com.bytedesk.core.rbac.role.RoleRestService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.department.DepartmentRestService;

@ExtendWith(MockitoExtension.class)
class MemberRestServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private AuthService authService;

    @Mock
    private RoleRestService roleRestService;

    @Mock
    private ThreadRestService threadRestService;

    @Mock
    private DepartmentRestService departmentRestService;

    @Mock
    private OrganizationRestService organizationRestService;

    @Mock
    private MessageService messageService;

    private MemberRestService memberRestService;

    @BeforeEach
    void setUp() {
        memberRestService = new MemberRestService(
                userService,
                memberRepository,
                modelMapper,
                uidUtils,
                authService,
                roleRestService,
                threadRestService,
                departmentRestService,
                organizationRestService,
                messageService);
    }

    @Test
    void createRejectsWhenOrganizationHasReachedMaxMembersEvenForSuperUser() {
        MemberRequest request = MemberRequest.builder()
                .nickname("Org Admin")
                .email("org-admin@example.com")
                .mobile("13800138000")
                .deptUid("dept-1")
                .build();
        request.setOrgUid("org-1");
        request.setCountry("86");

        UserEntity superUser = new UserEntity();
        superUser.setSuperUser(true);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setUid("org-1");
        organization.setName("Org One");
        organization.setMaxMembers(1);

        lenient().when(authService.getUser()).thenReturn(superUser);
        when(memberRepository.existsByEmailAndOrgUidAndDeletedFalse("org-admin@example.com", "org-1"))
                .thenReturn(false);
        when(memberRepository.existsByMobileAndCountryAndOrgUidAndDeletedFalse("13800138000", "86", "org-1"))
                .thenReturn(false);
        when(organizationRestService.findByUid("org-1")).thenReturn(Optional.of(organization));
        when(memberRepository.countByOrgUidAndDeletedFalse("org-1")).thenReturn(1L);

        OrgMaxMembersExceededException exception = assertThrows(
                OrgMaxMembersExceededException.class,
                () -> memberRestService.create(request));

        assertThat(exception.getOrgUid()).isEqualTo("org-1");
        assertThat(exception.getMaxMembers()).isEqualTo(1);
        assertThat(exception.getCurrentDistinctUsers()).isEqualTo(1L);
    }

    @Test
    void convertExcelToMemberRejectsWhenOrganizationHasReachedMaxMembers() {
        MemberExcelImport excel = new MemberExcelImport();
        excel.setNickname("Imported Member");
        excel.setEmail("imported@example.com");

        OrganizationEntity organization = new OrganizationEntity();
        organization.setUid("org-1");
        organization.setName("Org One");
        organization.setMaxMembers(1);

        when(memberRepository.existsByEmailAndOrgUidAndDeletedFalse("imported@example.com", "org-1"))
                .thenReturn(false);
        when(organizationRestService.findByUid("org-1")).thenReturn(Optional.of(organization));
        when(memberRepository.countByOrgUidAndDeletedFalse("org-1")).thenReturn(1L);

        OrgMaxMembersExceededException exception = assertThrows(
                OrgMaxMembersExceededException.class,
                () -> memberRestService.convertExcelToMember(excel, "org-1"));

        assertThat(exception.getOrgUid()).isEqualTo("org-1");
        assertThat(exception.getMaxMembers()).isEqualTo(1);
        assertThat(exception.getCurrentDistinctUsers()).isEqualTo(1L);
    }

        @Test
        void queryByUserUidUsesExplicitOrgUid() {
                MemberRequest request = MemberRequest.builder().build();
                request.setUserUid("user-1");
                request.setOrgUid("org-2");

                UserEntity memberUser = new UserEntity();
                memberUser.setUid("user-1");
                MemberEntity member = MemberEntity.builder()
                                .uid("member-1")
                                .orgUid("org-2")
                                .user(memberUser)
                                .build();

                when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-1", "org-2"))
                                .thenReturn(Optional.of(member));
                when(modelMapper.map(member, MemberResponse.class)).thenReturn(new MemberResponse());

                memberRestService.queryByUserUid(request);

                verify(memberRepository).findByUser_UidAndOrgUidAndDeletedFalse("user-1", "org-2");
        }

        @Test
        void queryByUserUidFallsBackToCurrentOrgUid() {
                MemberRequest request = MemberRequest.builder().build();
                request.setUserUid("user-2");

                UserEntity currentUser = new UserEntity();
                OrganizationEntity currentOrganization = new OrganizationEntity();
                currentOrganization.setUid("org-current");
                currentUser.setCurrentOrganization(currentOrganization);

                UserEntity memberUser = new UserEntity();
                memberUser.setUid("user-2");
                MemberEntity member = MemberEntity.builder()
                                .uid("member-2")
                                .orgUid("org-current")
                                .user(memberUser)
                                .build();

                when(authService.getUser()).thenReturn(currentUser);
                when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-2", "org-current"))
                                .thenReturn(Optional.of(member));
                when(modelMapper.map(member, MemberResponse.class)).thenReturn(new MemberResponse());

                memberRestService.queryByUserUid(request);

                verify(memberRepository).findByUser_UidAndOrgUidAndDeletedFalse("user-2", "org-current");
        }
}