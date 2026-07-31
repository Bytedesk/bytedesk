package com.bytedesk.service.agent_settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.settings_service.ServiceSettingsHelper;
import com.bytedesk.kbase.settings_trigger.TriggerSettingsHelper;
import com.bytedesk.service.agent.AgentRepository;
import com.bytedesk.service.message_leave_settings.MessageLeaveSettingsHelper;
import com.bytedesk.service.worktime_settings.WorktimeSettingEntity;
import com.bytedesk.service.worktime_settings.WorktimeSettingRequest;

@ExtendWith(MockitoExtension.class)
class AgentSettingsRestServiceTest {

    @Mock
    private AgentSettingsRepository agentSettingsRepository;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private AuthService authService;

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private ServiceSettingsHelper serviceSettingsHelper;

    @Mock
    private TriggerSettingsHelper triggerSettingsHelper;

    @Mock
    private MessageLeaveSettingsHelper messageLeaveSettingsHelper;

    private AgentSettingsRestService agentSettingsRestService;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        agentSettingsRestService = new AgentSettingsRestService(
                agentSettingsRepository,
                modelMapper,
                uidUtils,
                authService,
                agentRepository,
                serviceSettingsHelper,
                triggerSettingsHelper,
                messageLeaveSettingsHelper);
        when(agentSettingsRepository.save(any(AgentSettingsEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createShouldPersistHolidaySettingsEnabledIntoPublishedAndDraftWorktimeSettings() {
        AtomicInteger counter = new AtomicInteger(1);
        when(uidUtils.getUid()).thenAnswer(invocation -> "uid-" + counter.getAndIncrement());

        AgentSettingsRequest request = AgentSettingsRequest.builder()
                .orgUid("org-1")
                .userUid("user-1")
                .name("Agent template")
                .enabled(true)
                .worktimeSettings(WorktimeSettingRequest.builder()
                        .enabled(true)
                        .holidaySettingsEnabled(true)
                        .nonWorktimeTip("offline")
                        .build())
                .build();

        AgentSettingsResponse response = agentSettingsRestService.create(request);

        ArgumentCaptor<AgentSettingsEntity> entityCaptor = ArgumentCaptor.forClass(AgentSettingsEntity.class);
        verify(agentSettingsRepository).save(entityCaptor.capture());
        AgentSettingsEntity saved = entityCaptor.getValue();

        assertThat(saved.getWorktimeSettings()).isNotNull();
        assertThat(saved.getDraftWorktimeSettings()).isNotNull();
        assertThat(saved.getWorktimeSettings().getHolidaySettingsEnabled()).isTrue();
        assertThat(saved.getDraftWorktimeSettings().getHolidaySettingsEnabled()).isTrue();
        assertThat(saved.getWorktimeSettings().getOrgUid()).isEqualTo("org-1");
        assertThat(saved.getDraftWorktimeSettings().getUserUid()).isEqualTo("user-1");
        assertThat(response.getWorktimeSettings()).isNotNull();
        assertThat(response.getDraftWorktimeSettings()).isNotNull();
        assertThat(response.getWorktimeSettings().getHolidaySettingsEnabled()).isTrue();
        assertThat(response.getDraftWorktimeSettings().getHolidaySettingsEnabled()).isTrue();
    }

    @Test
    void updateShouldPersistHolidaySettingsEnabledIntoExistingDraftWorktimeSettings() {
        WorktimeSettingEntity published = WorktimeSettingEntity.builder()
                .uid("worktime-published-1")
                .enabled(true)
                .holidaySettingsEnabled(false)
                .build();
        WorktimeSettingEntity draft = WorktimeSettingEntity.builder()
                .uid("worktime-draft-1")
                .enabled(true)
                .holidaySettingsEnabled(false)
                .build();
        AgentSettingsEntity existing = AgentSettingsEntity.builder()
                .uid("settings-1")
                .orgUid("org-1")
                .userUid("user-1")
                .name("Agent template")
                .enabled(true)
                .worktimeSettings(published)
                .draftWorktimeSettings(draft)
                .build();
        when(agentSettingsRepository.findByUid("settings-1")).thenReturn(Optional.of(existing));

        AgentSettingsRequest request = AgentSettingsRequest.builder()
                .uid("settings-1")
                .worktimeSettings(WorktimeSettingRequest.builder()
                        .enabled(true)
                        .holidaySettingsEnabled(true)
                        .nonWorktimeTip("updated offline")
                        .build())
                .build();

        AgentSettingsResponse response = agentSettingsRestService.update(request);

        ArgumentCaptor<AgentSettingsEntity> entityCaptor = ArgumentCaptor.forClass(AgentSettingsEntity.class);
        verify(agentSettingsRepository).save(entityCaptor.capture());
        AgentSettingsEntity saved = entityCaptor.getValue();

        assertThat(saved.getDraftWorktimeSettings()).isNotNull();
        assertThat(saved.getDraftWorktimeSettings().getUid()).isEqualTo("worktime-draft-1");
        assertThat(saved.getDraftWorktimeSettings().getHolidaySettingsEnabled()).isTrue();
        assertThat(saved.getDraftWorktimeSettings().getNonWorktimeTip()).isEqualTo("updated offline");
        assertThat(saved.getWorktimeSettings().getHolidaySettingsEnabled()).isFalse();
        assertThat(saved.getHasUnpublishedChanges()).isTrue();
        assertThat(response.getDraftWorktimeSettings()).isNotNull();
        assertThat(response.getDraftWorktimeSettings().getHolidaySettingsEnabled()).isTrue();
    }
}