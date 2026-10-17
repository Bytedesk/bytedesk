package com.bytedesk.ticket.ticket_settings_visibility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.bytedesk.ticket.ticket_settings.TicketSettingsEntity;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRestService;

/**
 * 可见性设置统一解析入口测试：优先级、历史引用、草稿/发布、类型回退与空子实体。
 */
class TicketVisibilityServiceTest {

        @Test
        void resolvesByExplicitTicketSettingsUidFirst() {
                TicketSettingsRestService settings = Mockito.mock(TicketSettingsRestService.class);
                TicketVisibilityService service = new TicketVisibilityService(settings);

                TicketSettingsEntity custom = settings("settings-custom", "EXTERNAL",
                                visibility("DEPARTMENT_BASED", List.of("dept-a")));
                when(settings.findByUid("settings-custom")).thenReturn(Optional.of(custom));

                assertThat(service.resolve("org-1", "EXTERNAL", "settings-custom").getMode())
                                .isEqualTo("DEPARTMENT_BASED");
                // 默认设置未被查询（显式引用优先）
                Mockito.verify(settings, Mockito.never())
                                .findDefaultByOrgUidAndType(Mockito.anyString(), Mockito.anyString());
        }

        @Test
        void fallsBackToOrgDefaultWhenUidMissing() {
                TicketSettingsRestService settings = Mockito.mock(TicketSettingsRestService.class);
                TicketVisibilityService service = new TicketVisibilityService(settings);

                when(settings.findByUid("settings-gone")).thenReturn(Optional.empty());
                when(settings.findDefaultByOrgUidAndType("org-1", "INTERNAL"))
                                .thenReturn(Optional.of(settings("settings-default", "INTERNAL",
                                                visibility("DEPARTMENT_RESTRICTED", List.of()))));

                assertThat(service.resolve("org-1", "INTERNAL", "settings-gone").getMode())
                                .isEqualTo("DEPARTMENT_RESTRICTED");
        }

        @Test
        void fallsBackToOrgWideWhenNothingConfigured() {
                TicketSettingsRestService settings = Mockito.mock(TicketSettingsRestService.class);
                TicketVisibilityService service = new TicketVisibilityService(settings);

                when(settings.findByUid("settings-x")).thenReturn(Optional.empty());
                when(settings.findDefaultByOrgUidAndType("org-1", "EXTERNAL")).thenReturn(Optional.empty());

                TicketVisibilitySettingsData data = service.resolve("org-1", "EXTERNAL", "settings-x");
                assertThat(data.getMode()).isEqualTo("ORG_WIDE");
                assertThat(service.isDefaultFallback(data)).isTrue();
        }

        @Test
        void ignoresDraftAndUsesPublishedContentOnly() {
                TicketSettingsRestService settings = Mockito.mock(TicketSettingsRestService.class);
                TicketVisibilityService service = new TicketVisibilityService(settings);

                TicketSettingsEntity entity = settings("settings-1", "EXTERNAL",
                                visibility("DEPARTMENT_RESTRICTED", List.of()));
                entity.setDraftVisibilitySettings(
                                TicketVisibilitySettingsEntity.builder().content(TicketVisibilitySettingsData
                                                .builder().mode("ORG_WIDE").build()).build());
                when(settings.findByUid("settings-1")).thenReturn(Optional.of(entity));

                // 草稿 ORG_WIDE 不得影响解析：仍按已发布 DEPARTMENT_RESTRICTED
                assertThat(service.resolve("org-1", "EXTERNAL", "settings-1").getMode())
                                .isEqualTo("DEPARTMENT_RESTRICTED");
        }

        @Test
        void treatsNullVisibilitySubEntityAsFallbackToDefault() {
                TicketSettingsRestService settings = Mockito.mock(TicketSettingsRestService.class);
                TicketVisibilityService service = new TicketVisibilityService(settings);

                TicketSettingsEntity noVisibility = settings("settings-novis", "EXTERNAL", null);
                when(settings.findByUid("settings-novis")).thenReturn(Optional.of(noVisibility));
                when(settings.findDefaultByOrgUidAndType("org-1", "EXTERNAL"))
                                .thenReturn(Optional.of(settings("settings-default", "EXTERNAL",
                                                visibility("DEPARTMENT_BASED", List.of("dept-a")))));

                assertThat(service.resolve("org-1", "EXTERNAL", "settings-novis").getMode())
                                .isEqualTo("DEPARTMENT_BASED");
        }

        @Test
        void normalizesLegacyTypeValueBeforeDefaultLookup() {
                TicketSettingsRestService settings = Mockito.mock(TicketSettingsRestService.class);
                TicketVisibilityService service = new TicketVisibilityService(settings);

                when(settings.findByUid("settings-legacy")).thenReturn(Optional.empty());
                when(settings.findDefaultByOrgUidAndType("org-1", "INTERNAL"))
                                .thenReturn(Optional.of(settings("settings-default", "INTERNAL",
                                                visibility("DEPARTMENT_RESTRICTED", List.of()))));

                // 小写 internal → INTERNAL（TicketTypeEnum.fromValue 语义）
                assertThat(service.resolve("org-1", "internal", "settings-legacy").getMode())
                                .isEqualTo("DEPARTMENT_RESTRICTED");
                Mockito.verify(settings).findDefaultByOrgUidAndType("org-1", "INTERNAL");
        }

        @Test
        void returnsCopyThatDoesNotMutatePersistedContent() {
                TicketSettingsRestService settings = Mockito.mock(TicketSettingsRestService.class);
                TicketVisibilityService service = new TicketVisibilityService(settings);

                TicketVisibilitySettingsData content = TicketVisibilitySettingsData.builder()
                                .mode("DEPARTMENT_BASED")
                                .departmentUids(new java.util.ArrayList<>(List.of("dept-a", "dept-a")))
                                .build();
                TicketSettingsEntity entity = settings("settings-1", "EXTERNAL", null);
                entity.setVisibilitySettings(
                                TicketVisibilitySettingsEntity.builder().content(content).build());
                when(settings.findByUid("settings-1")).thenReturn(Optional.of(entity));

                TicketVisibilitySettingsData resolved = service.resolve("org-1", "EXTERNAL", "settings-1");

                // 解析结果已规范化（去重），但持久化 content 未被改动（无脏写）
                assertThat(resolved.getDepartmentUids()).containsExactly("dept-a");
                assertThat(content.getDepartmentUids()).hasSize(2);
        }

        private static TicketSettingsEntity settings(String uid, String type,
                        TicketVisibilitySettingsEntity visibility) {
                TicketSettingsEntity entity = TicketSettingsEntity.builder().build();
                entity.setUid(uid);
                entity.setOrgUid("org-1");
                entity.setType(type);
                if (visibility != null) {
                        entity.setVisibilitySettings(visibility);
                }
                return entity;
        }

        private static TicketVisibilitySettingsEntity visibility(String mode, List<String> departmentUids) {
                return TicketVisibilitySettingsEntity.builder()
                                .content(TicketVisibilitySettingsData.builder()
                                                .mode(mode)
                                                .departmentUids(departmentUids)
                                                .build())
                                .build();
        }
}
