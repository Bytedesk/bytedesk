package com.bytedesk.service.agent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.AgentCapacityExceededException;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.core.message.MessageService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRestService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.auto_reply.settings.AutoReplySettingsEntity;
import com.bytedesk.service.agent_settings.AgentSettingsRestService;
import com.bytedesk.service.agent_seat.AgentSeatDomainService;
import com.bytedesk.service.agent_seat.AgentSeatEntity;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AgentRestServiceTest {

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private MemberRestService memberRestService;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @Mock
    private BytedeskEventPublisher bytedeskEventPublisher;

    @Mock
    private ThreadRestService threadRestService;

    @Mock
    private AgentSettingsRestService agentSettingsRestService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private OrganizationRestService organizationRestService;

        @Mock
        private MessageService messageService;

        @Mock
        private AgentSeatDomainService agentSeatDomainService;

    private AgentRestService agentRestService;

    @BeforeEach
    void setUp() {
                agentRestService = spy(new AgentRestService(
                agentRepository,
                uidUtils,
                memberRestService,
                userService,
                authService,
                bytedeskEventPublisher,
                threadRestService,
                agentSettingsRestService,
                modelMapper,
                organizationRestService,
                messageService,
                agentSeatDomainService));
        doAnswer(invocation -> {
            AgentEntity entity = invocation.getArgument(0);
            return AgentResponse.builder()
                    .uid(entity.getUid())
                    .nickname(entity.getNickname())
                    .email(entity.getEmail())
                    .mobile(entity.getMobile())
                    .enabled(entity.getEnabled())
                    .build();
        }).when(agentRestService).convertToResponse(any(AgentEntity.class));
    }

    @Test
        void createShouldAssignManagedSeat() {
        UserEntity user = new UserEntity();
        user.setUid("user-1");

        MemberEntity member = new MemberEntity();
        member.setUid("member-1");
        member.setUser(user);
        member.setCountry("CN");

        AgentSeatEntity seat = AgentSeatEntity.builder()
                .uid("seat-1")
                .expireAt(java.time.ZonedDateTime.parse("2027-04-10T00:00:00+08:00"))
                .build();

        AgentRequest request = AgentRequest.builder()
                .uid("agent-1")
                .orgUid("org-1")
                .shopUid("shop-1")
                .memberUid("member-1")
                .nickname("Agent A")
                .mobile("13800138000")
                .email("agent-a@test.com")
                .build();

        OrganizationEntity organization = OrganizationEntity.builder()
                .uid("org-1")
                .maxAgents(5)
                .build();

        when(memberRestService.findByUid("member-1")).thenReturn(Optional.of(member));
        when(organizationRestService.findByUid("org-1")).thenReturn(Optional.of(organization));
        when(agentRepository.findByUserUidAndOrgUidAndDeletedFalse("user-1", "org-1")).thenReturn(Optional.empty());
        when(agentRepository.existsByUserUidAndOrgUidAndDeletedFalse("user-1", "org-1")).thenReturn(false);
        when(agentRepository.countByOrgUidAndDeletedFalse("org-1")).thenReturn(0L);
        when(agentSeatDomainService.hasManagedSeats("org-1")).thenReturn(true);
        when(agentSeatDomainService.hasAvailableSeat("org-1")).thenReturn(true);
        when(userService.ensureCurrentOrganization(user, "org-1")).thenReturn(user);
        when(userService.addRoleAgent(user)).thenReturn(user);
        when(agentSeatDomainService.assignSeatForAgent("org-1", "agent-1"))
                .thenReturn(Optional.of(seat));
        when(agentRepository.save(any(AgentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AgentResponse response = agentRestService.create(request);

        assertThat(response).isNotNull();
        verify(agentRepository).save(any(AgentEntity.class));
    }

    @Test
    void createShouldRejectWhenManagedSeatsAreExhausted() {
        UserEntity user = new UserEntity();
        user.setUid("user-1");

        MemberEntity member = new MemberEntity();
        member.setUid("member-1");
        member.setUser(user);

        AgentRequest request = AgentRequest.builder()
                .uid("agent-1")
                .orgUid("org-1")
                .memberUid("member-1")
                .nickname("Agent A")
                .mobile("13800138000")
                .email("agent-a@test.com")
                .build();

        OrganizationEntity organization = OrganizationEntity.builder()
                .uid("org-1")
                .maxAgents(5)
                .build();

        when(memberRestService.findByUid("member-1")).thenReturn(Optional.of(member));
        when(organizationRestService.findByUid("org-1")).thenReturn(Optional.of(organization));
        when(agentRepository.existsByUserUidAndOrgUidAndDeletedFalse("user-1", "org-1")).thenReturn(false);
        when(agentRepository.countByOrgUidAndDeletedFalse("org-1")).thenReturn(0L);
        when(agentSeatDomainService.isSeatEnabled()).thenReturn(true);
        when(agentSeatDomainService.hasManagedSeats("org-1")).thenReturn(true);
        when(agentSeatDomainService.hasAvailableSeat("org-1")).thenReturn(false);

        assertThatThrownBy(() -> agentRestService.create(request))
                .isInstanceOf(AgentCapacityExceededException.class)
                .hasMessage(I18Consts.I18N_AGENT_SEAT_LIMIT_EXCEEDED);

        verify(agentRepository, never()).save(any(AgentEntity.class));
    }

    @Test
    void createShouldSkipSeatValidationForDefaultOrganization() {
        UserEntity user = new UserEntity();
        user.setUid("user-1");

        MemberEntity member = new MemberEntity();
        member.setUid("member-1");
        member.setUser(user);
        member.setCountry("CN");

        AgentRequest request = AgentRequest.builder()
                .uid("agent-1")
                .orgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID)
                .memberUid("member-1")
                .nickname("Agent A")
                .mobile("13800138000")
                .email("agent-a@test.com")
                .build();

        when(memberRestService.findByUid("member-1")).thenReturn(Optional.of(member));
        when(agentRepository.existsByUserUidAndOrgUidAndDeletedFalse("user-1", BytedeskConsts.DEFAULT_ORGANIZATION_UID))
                .thenReturn(false);
        when(userService.ensureCurrentOrganization(user, BytedeskConsts.DEFAULT_ORGANIZATION_UID)).thenReturn(user);
        when(userService.addRoleAgent(user)).thenReturn(user);
        when(agentSeatDomainService.isSeatEnabled()).thenReturn(true);
        when(agentSeatDomainService.assignSeatForAgent(BytedeskConsts.DEFAULT_ORGANIZATION_UID, "agent-1"))
                .thenReturn(Optional.empty());
        when(agentRepository.save(any(AgentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AgentResponse response = agentRestService.create(request);

        assertThat(response).isNotNull();
        verify(agentSeatDomainService, never()).hasAvailableSeat(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        verify(agentRepository).save(any(AgentEntity.class));
    }

    @Test
    void updateShouldRebindMemberAndSyncUserReferences() {
        UserEntity oldUser = new UserEntity();
        oldUser.setUid("user-old");

        MemberEntity oldMember = new MemberEntity();
        oldMember.setUid("member-old");
        oldMember.setUser(oldUser);

        UserEntity newUser = new UserEntity();
        newUser.setUid("user-new");

        MemberEntity newMember = new MemberEntity();
        newMember.setUid("member-new");
        newMember.setUser(newUser);

        AutoReplySettingsEntity autoReplySettings = new AutoReplySettingsEntity();
        autoReplySettings.setUserUid("user-old");

        AgentEntity agent = AgentEntity.builder()
                .uid("agent-1")
                .orgUid("org-1")
                .nickname("Agent A")
                .mobile("13800138000")
                .email("agent-a@test.com")
                .member(oldMember)
                .userUid("user-old")
                .autoReplySettings(autoReplySettings)
                .build();

        AgentRequest request = AgentRequest.builder()
                .uid("agent-1")
                .orgUid("org-1")
                .nickname("Agent B")
                .mobile("13800138001")
                .email("agent-b@test.com")
                .description("updated")
                .memberUid("member-new")
                .build();

        when(agentRepository.findByUid("agent-1")).thenReturn(Optional.of(agent));
        when(memberRestService.findByUid("member-new")).thenReturn(Optional.of(newMember));
        when(agentRepository.findByUserUidAndOrgUidAndDeletedFalse("user-new", "org-1")).thenReturn(Optional.empty());
        when(agentRepository.existsByUserUidAndOrgUidAndUidNotAndDeletedFalse("user-old", "org-1", "agent-1")).thenReturn(false);
        when(userService.ensureCurrentOrganization(newUser, "org-1")).thenReturn(newUser);
        when(userService.ensureCurrentOrganization(oldUser, "org-1")).thenReturn(oldUser);
        when(userService.addRoleAgent(newUser)).thenReturn(newUser);
        when(userService.removeRoleAgent(oldUser)).thenReturn(oldUser);
        when(agentRepository.save(any(AgentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AgentResponse response = agentRestService.update(request);

        assertThat(response).isNotNull();
        assertThat(agent.getMember()).isSameAs(newMember);
        assertThat(agent.getUserUid()).isEqualTo("user-new");
        assertThat(agent.getAutoReplySettings().getUserUid()).isEqualTo("user-new");
        verify(userService).addRoleAgent(newUser);
        verify(userService).removeRoleAgent(oldUser);
    }

    @Test
    void updateShouldRejectBindingMemberAlreadyUsedByAnotherAgent() {
        UserEntity currentUser = new UserEntity();
        currentUser.setUid("user-current");

        MemberEntity currentMember = new MemberEntity();
        currentMember.setUid("member-current");
        currentMember.setUser(currentUser);

        UserEntity occupiedUser = new UserEntity();
        occupiedUser.setUid("user-occupied");

        MemberEntity occupiedMember = new MemberEntity();
        occupiedMember.setUid("member-occupied");
        occupiedMember.setUser(occupiedUser);

        AgentEntity currentAgent = AgentEntity.builder()
                .uid("agent-1")
                .orgUid("org-1")
                .nickname("Agent A")
                .mobile("13800138000")
                .email("agent-a@test.com")
                .member(currentMember)
                .userUid("user-current")
                .build();

        AgentEntity occupiedAgent = AgentEntity.builder()
                .uid("agent-2")
                .orgUid("org-1")
                .nickname("Agent Occupied")
                .mobile("13800138002")
                .email("agent-c@test.com")
                .member(occupiedMember)
                .userUid("user-occupied")
                .build();

        AgentRequest request = AgentRequest.builder()
                .uid("agent-1")
                .orgUid("org-1")
                .nickname("Agent B")
                .mobile("13800138001")
                .email("agent-b@test.com")
                .description("updated")
                .memberUid("member-occupied")
                .build();

        when(agentRepository.findByUid("agent-1")).thenReturn(Optional.of(currentAgent));
        when(memberRestService.findByUid("member-occupied")).thenReturn(Optional.of(occupiedMember));
        when(agentRepository.findByUserUidAndOrgUidAndDeletedFalse("user-occupied", "org-1"))
                .thenReturn(Optional.of(occupiedAgent));

        assertThatThrownBy(() -> agentRestService.update(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(com.bytedesk.service.constant.I18ServiceConsts.I18N_AGENT_EXISTS);

        verify(agentRepository, never()).save(any(AgentEntity.class));
        verify(userService, never()).addRoleAgent(any(UserEntity.class));
    }
}