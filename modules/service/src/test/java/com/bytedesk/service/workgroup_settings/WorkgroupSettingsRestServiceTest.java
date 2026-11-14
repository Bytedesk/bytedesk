package com.bytedesk.service.workgroup_settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.lenient;
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
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.bytedesk.ai.robot.RobotRepository;
import com.bytedesk.ai.robot.settings.RobotRoutingSettingsService;
import com.bytedesk.core.tag.TagRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.settings_service.ServiceSettingsHelper;
import com.bytedesk.kbase.settings_trigger.TriggerSettingsHelper;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.message_leave_settings.MessageLeaveSettingsHelper;
import com.bytedesk.service.workgroup.WorkgroupRepository;
import com.bytedesk.service.worktime_settings.WorktimeSettingEntity;
import com.bytedesk.service.worktime_settings.WorktimeSettingRequest;

@ExtendWith(MockitoExtension.class)
class WorkgroupSettingsRestServiceTest {

    @Mock
    private WorkgroupSettingsRepository workgroupSettingsRepository;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private ServiceSettingsHelper serviceSettingsHelper;

    @Mock
    private TriggerSettingsHelper triggerSettingsHelper;

    @Mock
    private MessageLeaveSettingsHelper messageLeaveSettingsHelper;

    @Mock
    private RobotRepository robotRepository;

    @Mock
    private WorkgroupRepository workgroupRepository;

    @Mock
    private RobotRoutingSettingsService robotRoutingSettingsService;

    @Mock
    private TagRestService tagRestService;

    @Mock
    private AgentRestService agentRestService;

    private WorkgroupSettingsRestService workgroupSettingsRestService;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        workgroupSettingsRestService = new WorkgroupSettingsRestService(
                workgroupSettingsRepository,
                modelMapper,
                uidUtils,
                serviceSettingsHelper,
                triggerSettingsHelper,
                messageLeaveSettingsHelper,
                robotRepository,
                workgroupRepository,
                robotRoutingSettingsService,
                tagRestService,
                agentRestService);

        AtomicInteger counter = new AtomicInteger(1);
        lenient().when(uidUtils.getUid()).thenAnswer(invocation -> "uid-" + counter.getAndIncrement());
        lenient().when(workgroupSettingsRepository.save(any(WorkgroupSettingsEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createShouldPersistHolidaySettingsEnabledIntoPublishedAndDraftWorktimeSettings() {
        WorkgroupSettingsRequest request = WorkgroupSettingsRequest.builder()
                .orgUid("org-1")
                .userUid("user-1")
                .name("Support template")
                .enabled(true)
                .worktimeSettings(WorktimeSettingRequest.builder()
                        .enabled(true)
                        .holidaySettingsEnabled(true)
                        .nonWorktimeTip("offline")
                        .build())
                .build();

        WorkgroupSettingsResponse response = workgroupSettingsRestService.create(request);

        ArgumentCaptor<WorkgroupSettingsEntity> entityCaptor = ArgumentCaptor.forClass(WorkgroupSettingsEntity.class);
        verify(workgroupSettingsRepository).save(entityCaptor.capture());
        WorkgroupSettingsEntity saved = entityCaptor.getValue();

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
        WorkgroupSettingsEntity existing = WorkgroupSettingsEntity.builder()
                .uid("settings-1")
                .orgUid("org-1")
                .userUid("user-1")
                .name("Support template")
                .enabled(true)
                .worktimeSettings(published)
                .draftWorktimeSettings(draft)
                .build();
        when(workgroupSettingsRepository.findByUid("settings-1")).thenReturn(Optional.of(existing));

        WorkgroupSettingsRequest request = WorkgroupSettingsRequest.builder()
                .uid("settings-1")
                .worktimeSettings(WorktimeSettingRequest.builder()
                        .enabled(true)
                        .holidaySettingsEnabled(true)
                        .nonWorktimeTip("updated offline")
                        .build())
                .build();

        WorkgroupSettingsResponse response = workgroupSettingsRestService.update(request);

        ArgumentCaptor<WorkgroupSettingsEntity> entityCaptor = ArgumentCaptor.forClass(WorkgroupSettingsEntity.class);
        verify(workgroupSettingsRepository).save(entityCaptor.capture());
        WorkgroupSettingsEntity saved = entityCaptor.getValue();

        assertThat(saved.getDraftWorktimeSettings()).isNotNull();
        assertThat(saved.getDraftWorktimeSettings().getUid()).isEqualTo("worktime-draft-1");
        assertThat(saved.getDraftWorktimeSettings().getHolidaySettingsEnabled()).isTrue();
        assertThat(saved.getDraftWorktimeSettings().getNonWorktimeTip()).isEqualTo("updated offline");
        assertThat(saved.getWorktimeSettings().getHolidaySettingsEnabled()).isFalse();
        assertThat(saved.getHasUnpublishedChanges()).isTrue();
        assertThat(response.getDraftWorktimeSettings()).isNotNull();
        assertThat(response.getDraftWorktimeSettings().getHolidaySettingsEnabled()).isTrue();
    }

    @Test
    void updatePersistsAndValidatesTagAgentBindings() {
        WorkgroupSettingsEntity existing = WorkgroupSettingsEntity.builder()
                .uid("settings-1")
                .orgUid("org-1")
                .build();
        when(workgroupSettingsRepository.findByUid("settings-1")).thenReturn(Optional.of(existing));
        when(tagRestService.findByUid("tag-1"))
                .thenReturn(Optional.of(com.bytedesk.core.tag.TagEntity.builder()
                        .uid("tag-1").orgUid("org-1").type("ROUTE").build()));
        when(agentRestService.findByUid("agent-1"))
                .thenReturn(Optional.of(AgentEntity.builder().uid("agent-1").orgUid("org-1").build()));

        WorkgroupSettingsResponse response = workgroupSettingsRestService.update(
                WorkgroupSettingsRequest.builder()
                        .uid("settings-1")
                        .tagAgentBindings(java.util.Map.of("tag-1", "agent-1"))
                        .build());

        assertThat(response.getTagAgentBindings()).containsEntry("tag-1", "agent-1");
        assertThat(existing.getTagAgentBindings()).containsEntry("tag-1", "agent-1");
    }

    @Test
    void updateClearsTagAgentBindingsWhenEmptyMapProvided() {
        WorkgroupSettingsEntity existing = WorkgroupSettingsEntity.builder()
                .uid("settings-1")
                .orgUid("org-1")
                .tagAgentBindings(new java.util.LinkedHashMap<>(java.util.Map.of("tag-1", "agent-1")))
                .build();
        when(workgroupSettingsRepository.findByUid("settings-1")).thenReturn(Optional.of(existing));

        WorkgroupSettingsResponse response = workgroupSettingsRestService.update(
                WorkgroupSettingsRequest.builder()
                        .uid("settings-1")
                        .tagAgentBindings(java.util.Map.of())
                        .build());

        assertThat(response.getTagAgentBindings()).isEmpty();
        assertThat(existing.getTagAgentBindings()).isEmpty();
    }

    @Test
    void updateRejectsRouteBindingForForeignOrgTag() {
        WorkgroupSettingsEntity existing = WorkgroupSettingsEntity.builder()
                .uid("settings-1")
                .orgUid("org-1")
                .build();
        when(workgroupSettingsRepository.findByUid("settings-1")).thenReturn(Optional.of(existing));
        when(tagRestService.findByUid("tag-1"))
                .thenReturn(Optional.of(com.bytedesk.core.tag.TagEntity.builder()
                        .uid("tag-1").orgUid("other-org").type("ROUTE").build()));

        assertThatThrownBy(() -> workgroupSettingsRestService.update(
                WorkgroupSettingsRequest.builder()
                        .uid("settings-1")
                        .tagAgentBindings(java.util.Map.of("tag-1", "agent-1"))
                        .build()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createDefaultsTagRoutingToFalseAndPersistsExplicitTrue() {
        WorkgroupSettingsResponse defaultResponse = workgroupSettingsRestService.create(
                WorkgroupSettingsRequest.builder().name("Default").build());
        assertThat(defaultResponse.getTagRoutingEnabled()).isFalse();

        WorkgroupSettingsResponse enabledResponse = workgroupSettingsRestService.create(
                WorkgroupSettingsRequest.builder().name("Tag routing").tagRoutingEnabled(true).build());
        assertThat(enabledResponse.getTagRoutingEnabled()).isTrue();
    }

    @Test
    void updatePreservesOmittedTagRoutingValueAndAppliesExplicitFalse() {
        WorkgroupSettingsEntity existing = WorkgroupSettingsEntity.builder()
                .uid("settings-1")
                .tagRoutingEnabled(true)
                .build();
        when(workgroupSettingsRepository.findByUid("settings-1")).thenReturn(Optional.of(existing));

        WorkgroupSettingsResponse unchanged = workgroupSettingsRestService.update(
                WorkgroupSettingsRequest.builder().uid("settings-1").build());
        assertThat(unchanged.getTagRoutingEnabled()).isTrue();

        WorkgroupSettingsResponse disabled = workgroupSettingsRestService.update(
                WorkgroupSettingsRequest.builder().uid("settings-1").tagRoutingEnabled(false).build());
        assertThat(disabled.getTagRoutingEnabled()).isFalse();
    }

    @Test
    void publishDoesNotResetTopLevelTagRoutingSetting() {
        WorkgroupSettingsEntity existing = WorkgroupSettingsEntity.builder()
                .uid("settings-1")
                .tagRoutingEnabled(true)
                .build();
        when(workgroupSettingsRepository.findByUid("settings-1")).thenReturn(Optional.of(existing));

        WorkgroupSettingsResponse response = workgroupSettingsRestService.publish("settings-1");

        assertThat(response.getTagRoutingEnabled()).isTrue();
        assertThat(existing.getTagRoutingEnabled()).isTrue();
    }

    @Test
    void optimisticLockConflictIsReportedInsteadOfReturningUnchangedSettings() {
        reset(uidUtils, workgroupSettingsRepository);
        WorkgroupSettingsEntity stale = WorkgroupSettingsEntity.builder()
                .uid("settings-1")
                .tagRoutingEnabled(true)
                .build();
        ObjectOptimisticLockingFailureException conflict = new ObjectOptimisticLockingFailureException(
                WorkgroupSettingsEntity.class, "settings-1");

        assertThatThrownBy(() -> workgroupSettingsRestService.handleOptimisticLockingFailureException(conflict, stale))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class)
                .hasMessageContaining("settings-1");
    }
}