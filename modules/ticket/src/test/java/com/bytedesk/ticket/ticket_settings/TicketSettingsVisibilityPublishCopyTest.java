package com.bytedesk.ticket.ticket_settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.PlatformTransactionManager;

import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.email_provider.EmailProviderRepository;
import com.bytedesk.core.email_push.EmailPushSendService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.sms_push.SmsPushSendService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.form.FormRepository;
import com.bytedesk.ticket.process.ProcessRepository;
import com.bytedesk.ticket.ticket_settings_binding.TicketSettingsBindingRepository;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilityCategoryRuleData;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilityModeEnum;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilitySettingsData;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilitySettingsEntity;

/**
 * G1 防回归：发布链路复制可见性设置时不得丢失分类规则的 departmentUids。
 *
 * <p>历史缺陷（2026-09-11 规划 G1）：{@code copyVisibilitySettingsData} 复制
 * {@code DEPARTMENT_BASED} 规则时漏掉 {@code departmentUids}，随后 {@code normalize()}
 * 将空部门规则退化为 {@code ORG_WIDE}，导致「选择部门=客服部」等配置发布后静默失效、全员可见。
 * 若回归，下方断言中规则 visibility 会退化为 ORG_WIDE 而测试失败。
 */
class TicketSettingsVisibilityPublishCopyTest {

        @Test
        void copyVisibilitySettingsDataPreservesTopLevelDepartmentUids() {
                TicketSettingsRestService service = newService();

                TicketVisibilitySettingsData copied = invokeCopyVisibilitySettingsData(service,
                                TicketVisibilitySettingsData.builder()
                                                .mode(TicketVisibilityModeEnum.DEPARTMENT_BASED.name())
                                                .departmentUids(List.of("dept-kefu", "dept-support"))
                                                .build());

                assertThat(copied.getMode()).isEqualTo(TicketVisibilityModeEnum.DEPARTMENT_BASED.name());
                assertThat(copied.getDepartmentUids()).containsExactly("dept-kefu", "dept-support");
                assertThat(copied.getCategoryRules()).isEmpty();
        }

        @Test
        void copyVisibilitySettingsDataDegradesTopLevelDepartmentBasedWhenDepartmentUidsEmpty() {
                TicketSettingsRestService service = newService();

                TicketVisibilitySettingsData copied = invokeCopyVisibilitySettingsData(service,
                                TicketVisibilitySettingsData.builder()
                                                .mode(TicketVisibilityModeEnum.DEPARTMENT_BASED.name())
                                                .departmentUids(List.of())
                                                .build());

                assertThat(copied.getMode()).isEqualTo(TicketVisibilityModeEnum.ORG_WIDE.name());
                assertThat(copied.getDepartmentUids()).isEmpty();
        }

    @Test
    void copyVisibilitySettingsDataPreservesDepartmentBasedRuleDepartmentUids() {
        TicketSettingsRestService service = newService();

        TicketVisibilitySettingsData copied = invokeCopyVisibilitySettingsData(service,
                TicketVisibilitySettingsData.builder()
                        .mode(TicketVisibilityModeEnum.CATEGORY_BASED.name())
                        .categoryRules(List.of(TicketVisibilityCategoryRuleData.builder()
                                .categoryUid("cat-support")
                                .visibility(TicketVisibilityModeEnum.DEPARTMENT_BASED.name())
                                .departmentUids(List.of("dept-kefu", "dept-xiaoshou"))
                                .build()))
                        .build());

        assertThat(copied.getMode()).isEqualTo(TicketVisibilityModeEnum.CATEGORY_BASED.name());
        assertThat(copied.getCategoryRules()).hasSize(1);
        TicketVisibilityCategoryRuleData rule = copied.getCategoryRules().get(0);
        // 核心断言：发布复制必须保留部门白名单；且回归时 normalize() 会把规则退化成 ORG_WIDE
        assertThat(rule.getVisibility()).isEqualTo(TicketVisibilityModeEnum.DEPARTMENT_BASED.name());
        assertThat(rule.getDepartmentUids()).containsExactlyInAnyOrder("dept-kefu", "dept-xiaoshou");
    }

    @Test
    void copyVisibilitySettingsCopiesDraftContentToPublishedEntity() {
        TicketSettingsRestService service = newService();

        // 模拟 publish() 中发布版可见性设置的复制：draft -> published（entity 级）
        TicketVisibilitySettingsEntity draft = TicketVisibilitySettingsEntity.builder()
                .uid("visibility-draft-1")
                .content(TicketVisibilitySettingsData.builder()
                        .mode(TicketVisibilityModeEnum.CATEGORY_BASED.name())
                        .categoryRules(List.of(TicketVisibilityCategoryRuleData.builder()
                                .categoryUid("cat-support")
                                .visibility(TicketVisibilityModeEnum.DEPARTMENT_BASED.name())
                                .departmentUids(List.of("dept-kefu"))
                                .build()))
                        .build())
                .build();
        TicketVisibilitySettingsEntity published = TicketVisibilitySettingsEntity.builder()
                .uid("visibility-published-1")
                .build();

        invokeCopyVisibilitySettings(service, draft, published);

        assertThat(published.getContent()).isNotNull();
        assertThat(published.getContent().getMode())
                .isEqualTo(TicketVisibilityModeEnum.CATEGORY_BASED.name());
        assertThat(published.getContent().getCategoryRules()).hasSize(1);
        assertThat(published.getContent().getCategoryRules().get(0).getVisibility())
                .isEqualTo(TicketVisibilityModeEnum.DEPARTMENT_BASED.name());
        assertThat(published.getContent().getCategoryRules().get(0).getDepartmentUids())
                .containsExactly("dept-kefu");
    }

    @Test
    void copyVisibilitySettingsDataStillDegradesToOrgWideWhenDepartmentUidsEmpty() {
        TicketSettingsRestService service = newService();

        // 既有语义保持：选择部门但未选任何部门 -> 退化为公司内部可见（触发告警分支）
        TicketVisibilitySettingsData copied = invokeCopyVisibilitySettingsData(service,
                TicketVisibilitySettingsData.builder()
                        .mode(TicketVisibilityModeEnum.CATEGORY_BASED.name())
                        .categoryRules(List.of(TicketVisibilityCategoryRuleData.builder()
                                .categoryUid("cat-empty")
                                .visibility(TicketVisibilityModeEnum.DEPARTMENT_BASED.name())
                                .departmentUids(List.of())
                                .build()))
                        .build());

        assertThat(copied.getCategoryRules()).hasSize(1);
        assertThat(copied.getCategoryRules().get(0).getVisibility())
                .isEqualTo(TicketVisibilityModeEnum.ORG_WIDE.name());
    }

    private static TicketSettingsRestService newService() {
        return new TicketSettingsRestService(
                mock(TicketSettingsRepository.class),
                mock(TicketSettingsBindingRepository.class),
                mock(ModelMapper.class),
                mock(UidUtils.class),
                mock(AuthService.class),
                mock(ProcessRepository.class),
                mock(FormRepository.class),
                mock(EmailProviderRepository.class),
                mock(EmailPushSendService.class),
                mock(SmsPushSendService.class),
                mock(CategoryRestService.class),
                mock(PlatformTransactionManager.class));
    }

    private static TicketVisibilitySettingsData invokeCopyVisibilitySettingsData(
            TicketSettingsRestService service, TicketVisibilitySettingsData source) {
        try {
            Method method = TicketSettingsRestService.class.getDeclaredMethod(
                    "copyVisibilitySettingsData", TicketVisibilitySettingsData.class);
            method.setAccessible(true);
            return (TicketVisibilitySettingsData) method.invoke(service, source);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex.getTargetException());
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void invokeCopyVisibilitySettings(TicketSettingsRestService service,
            TicketVisibilitySettingsEntity source, TicketVisibilitySettingsEntity target) {
        try {
            Method method = TicketSettingsRestService.class.getDeclaredMethod(
                    "copyVisibilitySettings",
                    TicketVisibilitySettingsEntity.class, TicketVisibilitySettingsEntity.class);
            method.setAccessible(true);
            method.invoke(service, source, target);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex.getTargetException());
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
