package com.bytedesk.ticket.ticket;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;

import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;
import com.bytedesk.ticket.ticket_settings.TicketSettingsEntity;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRepository;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilityModeEnum;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilitySettingsData;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilitySettingsEntity;

/**
 * 阶段1（缺口A）真实 JPA 回归：在 H2 上执行 TicketSpecification 可见性谓词，
 * 断言返回的工单 uid 集合与 totalElements，而不是仅验证 CriteriaBuilder 调用。
 *
 * <p>覆盖规划 4.1 的核心场景：
 * <ul>
 * <li>原始主诉：自定义设置 DEPARTMENT_BASED + 默认 ORG_WIDE，处理人（member uid assignee）不在允许部门 → 仍可见</li>
 * <li>反向权限：同工作组非受理人/非创建人/非所选部门成员 → 不可见（默认宽松不能导致自定义谓词被跳过）</li>
 * <li>默认严格 + 自定义宽松：详情口径的工单不再被列表漏掉</li>
 * <li>历史设置引用：软删除设置仍按其内容判定；未知 uid / 空引用回退类型默认口径</li>
 * <li>混合类型并集：全部 = 内部 ∪ 外部，total 同口径</li>
 * </ul>
 */
@DataJpaTest
@AutoConfigureTestDatabase
// 扫描 ticket 全部实体（settings 各子实体互相引用，需整体进同一持久化单元）
// + TicketSettingsEntity 关联的 FormEntity（service 模块）与 Ticket 关联的 UploadEntity（core 模块）
@EntityScan(basePackages = { "com.bytedesk.ticket",
        "com.bytedesk.service.form",
        "com.bytedesk.core.upload",
        "com.bytedesk.core.base" })
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.main.banner-mode=off"
})
class TicketVisibilitySettingsScopedJpaTest {

    private static final String ORG = "org-vis";
    private static final String USER_ASSIGNEE = "user-assignee";
    // 与真实自动分配一致：member uid 含下划线（如 df_mb_uid），回归 escapeLike 的 LIKE 转义
    private static final String MEMBER_ASSIGNEE = "df_mb_uid";
    private static final String USER_VIEWER = "user-viewer";
    private static final String USER_DEPT_ALLOWED = "user-dept-allowed";
    private static final String DEPT_KEFU = "dept-kefu";
    private static final String DEPT_OTHER = "dept-other";

    /**
     * 空配置：避免 @DataJpaTest 引导加载应用主类（跨模块实体扫描导致持久化单元冲突）。
     * 注册 ApplicationContextHolder 以支持实体监听器（TicketEntityListener）静态获取 bean。
     */
    @SpringBootConfiguration
    @EnableAutoConfiguration
    // 仅排除引用 core 实体（UserOrganizationRoleEntity）的仓储：该实体不在本测试实体扫描范围内
    @EnableJpaRepositories(basePackages = "com.bytedesk.ticket",
            excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                    type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
                    classes = com.bytedesk.ticket.ticket.assignment.TicketUserOrgRoleRepository.class))
    static class TestConfig {
        @Bean
        ApplicationContextHolder applicationContextHolder() {
            return new ApplicationContextHolder();
        }

        @Bean
        com.bytedesk.core.config.BytedeskEventPublisher bytedeskEventPublisher(
                org.springframework.context.ApplicationEventPublisher publisher) {
            return new com.bytedesk.core.config.BytedeskEventPublisher(publisher);
        }
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketSettingsRepository ticketSettingsRepository;

    private String settingsStrictUid;
    private String settingsLooseUid;
    private String settingsSoftDeletedUid;
    private String settingsUnknownUid;

    @BeforeEach
    void seed() {
        // 自定义设置：指定部门可见（仅 dept-kefu），默认设置为 ORG_WIDE
        TicketSettingsEntity strict = ticketSettingsRepository
                .save(TicketSettingsEntity.builder()
                        .uid("settings-strict")
                        .orgUid(ORG)
                        .type(TicketTypeEnum.EXTERNAL.name())
                        .isDefault(false)
                        .visibilitySettings(TicketVisibilitySettingsEntity.builder()
                                .uid("vis-strict")
                                .orgUid(ORG)
                                .content(TicketVisibilitySettingsData.builder()
                                        .mode(TicketVisibilityModeEnum.DEPARTMENT_BASED.name())
                                        .departmentUids(List.of(DEPT_KEFU))
                                        .build())
                                .build())
                        .build());
        settingsStrictUid = strict.getUid();

        // 宽松自定义设置（ORG_WIDE），用于“默认严格+自定义宽松”场景
        TicketSettingsEntity loose = ticketSettingsRepository
                .save(TicketSettingsEntity.builder()
                        .uid("settings-loose")
                        .orgUid(ORG)
                        .type(TicketTypeEnum.EXTERNAL.name())
                        .isDefault(false)
                        .visibilitySettings(TicketVisibilitySettingsEntity.builder()
                                .uid("vis-loose")
                                .orgUid(ORG)
                                .content(TicketVisibilitySettingsData.builder()
                                        .mode(TicketVisibilityModeEnum.ORG_WIDE.name())
                                        .build())
                                .build())
                        .build());
        settingsLooseUid = loose.getUid();

        // 软删除设置：内容 DEPARTMENT_BASED(dept-kefu)；findByUidIn 不过滤软删除 → 仍按其内容判定
        TicketSettingsEntity softDeleted = ticketSettingsRepository
                .save(TicketSettingsEntity.builder()
                        .uid("settings-softdeleted")
                        .orgUid(ORG)
                        .type(TicketTypeEnum.EXTERNAL.name())
                        .isDefault(false)
                        .deleted(true)
                        .visibilitySettings(TicketVisibilitySettingsEntity.builder()
                                .uid("vis-softdeleted")
                                .orgUid(ORG)
                                .content(TicketVisibilitySettingsData.builder()
                                        .mode(TicketVisibilityModeEnum.DEPARTMENT_BASED.name())
                                        .departmentUids(List.of(DEPT_KEFU))
                                        .build())
                                .build())
                        .build());
        settingsSoftDeletedUid = softDeleted.getUid();

        // 未知设置 uid（模拟已被物理删除的引用）
        settingsUnknownUid = "settings-physically-deleted";

        // 默认外部设置：ORG_WIDE
        ticketSettingsRepository.save(TicketSettingsEntity.builder()
                .uid("settings-default-external")
                .orgUid(ORG)
                .type(TicketTypeEnum.EXTERNAL.name())
                .isDefault(true)
                .visibilitySettings(TicketVisibilitySettingsEntity.builder()
                        .uid("vis-default-external")
                        .orgUid(ORG)
                        .content(TicketVisibilitySettingsData.builder()
                                .mode(TicketVisibilityModeEnum.ORG_WIDE.name())
                                .build())
                        .build())
                .build());

        // 工单数据：assignee 为自动分配写入的 member uid（紧凑 JSON）
        // ticket-assignee：原始主诉场景，处理人在 dept-other（不在 dept-kefu）
        persistTicket("ticket-assignee", TicketTypeEnum.EXTERNAL, settingsStrictUid, USER_VIEWER,
                compactAssigneeJson(MEMBER_ASSIGNEE), null);
        // ticket-plain：同设置下的普通工单（报告人/受理人均非当前查看者）
        persistTicket("ticket-plain", TicketTypeEnum.EXTERNAL, settingsStrictUid, "user-reporter-plain",
                compactAssigneeJson("member-other"), null);
        // ticket-loose：宽松自定义设置下的普通工单（默认严格场景用）
        persistTicket("ticket-loose", TicketTypeEnum.EXTERNAL, settingsLooseUid, "user-reporter-loose",
                compactAssigneeJson("member-loose"), null);
        // ticket-softdeleted：引用软删除设置（内容受限）
        persistTicket("ticket-softdeleted", TicketTypeEnum.EXTERNAL, settingsSoftDeletedUid, "user-reporter-sd",
                compactAssigneeJson("member-sd"), null);
        // ticket-unknown：引用不存在的设置 uid → 回退默认（ORG_WIDE 可见）
        persistTicket("ticket-unknown", TicketTypeEnum.EXTERNAL, settingsUnknownUid, "user-reporter-unknown",
                compactAssigneeJson("member-unknown"), null);
        // ticket-nosettings：未绑定设置 → 回退默认（ORG_WIDE 可见）
        persistTicket("ticket-nosettings", TicketTypeEnum.EXTERNAL, null, "user-reporter-none",
                compactAssigneeJson("member-none"), null);
        // ticket-internal：内部工单（默认 INTERNAL 无设置 → ORG_WIDE）
        persistTicket("ticket-internal", TicketTypeEnum.INTERNAL, null, "user-reporter-internal",
                compactAssigneeJson("member-internal"), null);

        entityManager.flush();
        entityManager.clear();
    }

    private void persistTicket(String uid, TicketTypeEnum type, String settingsUid,
            String reporterUserUid, String assigneeJson, String departmentUid) {
        entityManager.persist(TicketEntity.builder()
                .uid(uid)
                .orgUid(ORG)
                .type(type.name())
                .status("ASSIGNED")
                .ticketSettingsUid(settingsUid)
                .userUid(reporterUserUid)
                .assignee(assigneeJson)
                .departmentUid(departmentUid)
                .deleted(false)
                .build());
    }

    private static String compactAssigneeJson(String uid) {
        return "{\"uid\":\"" + uid + "\",\"nickname\":\"agent\"}";
    }

    /** 构造与 enrichVisibilityContext 相同的服务端上下文（模拟 workbench 查询请求）。 */
    private TicketRequest buildRequest(String userUid, String memberUid, String deptUid) {
        // 类型默认上下文：EXTERNAL 默认 ORG_WIDE、INTERNAL 默认 ORG_WIDE
        TicketVisibilityQueryContext internalDefault = TicketVisibilityQueryContext.builder()
                .mode(TicketVisibilityModeEnum.ORG_WIDE.name()).restricted(false).build();
        TicketVisibilityQueryContext externalDefault = TicketVisibilityQueryContext.builder()
                .mode(TicketVisibilityModeEnum.ORG_WIDE.name()).restricted(false).build();

        // 已解析设置上下文：严格/宽松/软删除（与 loadSettingsVisibilityContexts 行为一致，软删除不过滤）
        Map<String, TicketVisibilityQueryContext> externalSettings = new HashMap<>();
        externalSettings.put(settingsStrictUid, TicketVisibilityQueryContext.builder()
                .mode(TicketVisibilityModeEnum.DEPARTMENT_BASED.name())
                .restricted(true)
                .allowedDepartmentUids(List.of(DEPT_KEFU))
                .build());
        externalSettings.put(settingsLooseUid, TicketVisibilityQueryContext.builder()
                .mode(TicketVisibilityModeEnum.ORG_WIDE.name()).restricted(false).build());
        externalSettings.put(settingsSoftDeletedUid, TicketVisibilityQueryContext.builder()
                .mode(TicketVisibilityModeEnum.DEPARTMENT_BASED.name())
                .restricted(true)
                .allowedDepartmentUids(List.of(DEPT_KEFU))
                .build());
        // 未知 uid 不在已解析集合中 → 未命中分支按默认口径

        TicketRequest request = new TicketRequest();
        request.setOrgUid(ORG);
        request.setVisibilityRestricted(true);
        request.setVisibilityCurrentUserUid(userUid);
        request.setVisibilityCurrentUserMemberUid(memberUid);
        request.setVisibilityCurrentUserDepartmentUid(deptUid);
        request.setVisibilityInternalContext(internalDefault);
        request.setVisibilityExternalContext(externalDefault);
        request.setVisibilityInternalSettingsContexts(new HashMap<>());
        request.setVisibilityExternalSettingsContexts(externalSettings);
        return request;
    }

    private Page<TicketEntity> query(TicketRequest request) {
        return ticketRepository.findAll(TicketSpecification.search(request, null), PageRequest.of(0, 50));
    }

    private List<String> queryUids(TicketRequest request) {
        return query(request).stream().map(ticket -> ticket.getUid()).sorted().toList();
    }

    @Test
    void assigneeOutsideAllowedDepartmentStillSeesAutoAssignedTicket() {
        // 原始主诉：自动分配后 assignee=member uid，处理人不在所选部门 → 豁免可见
        TicketRequest request = buildRequest(USER_ASSIGNEE, MEMBER_ASSIGNEE, DEPT_OTHER);
        List<String> uids = queryUids(request);
        assertThat(uids).contains("ticket-assignee");
        assertThat(query(request).getTotalElements()).isEqualTo(uids.size());
    }

    @Test
    void plainViewerOutsideAllowedDepartmentDoesNotSeeStrictSettingsTickets() {
        // 反向权限：默认 ORG_WIDE 不得导致受限自定义设置的工单对普通查看者暴露
        TicketRequest request = buildRequest(USER_VIEWER, "member-viewer", DEPT_OTHER);
        List<String> uids = queryUids(request);
        assertThat(uids).doesNotContain("ticket-plain", "ticket-softdeleted");
        // 未命中（未知 uid/未绑定）回退默认 ORG_WIDE；宽松设置 ORG_WIDE；内部默认可见
        assertThat(uids).contains("ticket-loose", "ticket-unknown", "ticket-nosettings", "ticket-internal");
    }

    @Test
    void allowedDepartmentViewerSeesStrictSettingsTickets() {
        // 所选部门成员可见受限设置下的工单（部门规则本身生效）
        TicketRequest request = buildRequest(USER_DEPT_ALLOWED, "member-dept-allowed", DEPT_KEFU);
        List<String> uids = queryUids(request);
        assertThat(uids).contains("ticket-plain", "ticket-softdeleted", "ticket-assignee");
    }

    @Test
    void strictDefaultWithLooseCustomDoesNotHideTicketsFromPlainViewer() {
        // 默认严格 + 自定义宽松：详情口径（工单绑定设置）可见 → 列表不得漏
        Map<String, TicketVisibilityQueryContext> externalSettings = new HashMap<>();
        externalSettings.put(settingsLooseUid, TicketVisibilityQueryContext.builder()
                .mode(TicketVisibilityModeEnum.ORG_WIDE.name()).restricted(false).build());
        TicketRequest request = new TicketRequest();
        request.setOrgUid(ORG);
        request.setVisibilityRestricted(true);
        request.setVisibilityCurrentUserUid(USER_VIEWER);
        request.setVisibilityCurrentUserMemberUid("member-viewer");
        request.setVisibilityCurrentUserDepartmentUid(DEPT_OTHER);
        request.setVisibilityInternalContext(TicketVisibilityQueryContext.builder()
                .mode(TicketVisibilityModeEnum.ORG_WIDE.name()).restricted(false).build());
        request.setVisibilityExternalContext(TicketVisibilityQueryContext.builder()
                .mode(TicketVisibilityModeEnum.DEPARTMENT_BASED.name())
                .restricted(true)
                .allowedDepartmentUids(List.of(DEPT_KEFU))
                .build());
        request.setVisibilityInternalSettingsContexts(new HashMap<>());
        request.setVisibilityExternalSettingsContexts(externalSettings);

        List<String> uids = queryUids(request);
        // ticket-loose 绑定宽松设置：普通查看者可见（列表与详情口径一致）
        assertThat(uids).contains("ticket-loose");
        // 未绑定/未知引用走默认严格口径：不可见。ticket-assignee 报告人即当前查看者（USER_VIEWER），
        // 命中 reporterSelf 豁免可见，属预期行为
        assertThat(uids).doesNotContain("ticket-nosettings", "ticket-unknown", "ticket-plain");
        assertThat(uids).contains("ticket-assignee");
    }

    @Test
    void deletedTicketsAreNeverVisible() {
        // org/deleted 条件不被豁免绕过
        entityManager.persist(TicketEntity.builder()
                .uid("ticket-deleted")
                .orgUid(ORG)
                .type(TicketTypeEnum.EXTERNAL.name())
                .status("ASSIGNED")
                .ticketSettingsUid(settingsStrictUid)
                .userUid(USER_VIEWER)
                .assignee(compactAssigneeJson(MEMBER_ASSIGNEE))
                .deleted(true)
                .build());
        entityManager.flush();

        TicketRequest request = buildRequest(USER_ASSIGNEE, MEMBER_ASSIGNEE, DEPT_OTHER);
        assertThat(queryUids(request)).doesNotContain("ticket-deleted");
    }

    @Test
    void mixedListUnionMatchesSingleTypeQueries() {
        // 查询形态：全部 = 内部 ∪ 外部（同数据集、同过滤条件下 uid 集合一致，total 同口径）
        TicketRequest mixed = buildRequest(USER_VIEWER, "member-viewer", DEPT_OTHER);
        List<String> mixedUids = queryUids(mixed);

        TicketRequest externalOnly = buildRequest(USER_VIEWER, "member-viewer", DEPT_OTHER);
        externalOnly.setType(TicketTypeEnum.EXTERNAL.name());
        List<String> externalUids = queryUids(externalOnly);

        TicketRequest internalOnly = buildRequest(USER_VIEWER, "member-viewer", DEPT_OTHER);
        internalOnly.setType(TicketTypeEnum.INTERNAL.name());
        List<String> internalUids = queryUids(internalOnly);

        assertThat(mixedUids).containsExactlyInAnyOrderElementsOf(
                new java.util.ArrayList<>(externalUids) {
                    {
                        addAll(internalUids);
                    }
                });
        assertThat(query(mixed).getTotalElements())
                .isEqualTo(query(externalOnly).getTotalElements() + query(internalOnly).getTotalElements());
    }

    @Test
    void orgIsolationNotBypassedByExemption() {
        // 跨组织工单不受本组织可见性上下文影响（org 谓词优先）
        persistTicketOrg("ticket-other-org", "org-other", settingsStrictUid);
        entityManager.flush();

        TicketRequest request = buildRequest(USER_ASSIGNEE, MEMBER_ASSIGNEE, DEPT_OTHER);
        request.setOrgUid("org-other");
        assertThat(queryUids(request)).doesNotContain("ticket-assignee");
    }

    private void persistTicketOrg(String uid, String orgUid, String settingsUid) {
        entityManager.persist(TicketEntity.builder()
                .uid(uid)
                .orgUid(orgUid)
                .type(TicketTypeEnum.EXTERNAL.name())
                .status("ASSIGNED")
                .ticketSettingsUid(settingsUid)
                .userUid("user-reporter-other-org")
                .assignee(compactAssigneeJson("member-other-org"))
                .deleted(false)
                .build());
    }

    @Test
    void explicitSettingsUidQueryNarrowsResultSet() {
        // 显式 settingsUid 查询：只返回绑定该设置的工单
        TicketRequest request = buildRequest(USER_DEPT_ALLOWED, "member-dept-allowed", DEPT_KEFU);
        request.setTicketSettingsUid(settingsStrictUid);
        List<String> uids = queryUids(request);
        assertThat(uids).containsExactlyInAnyOrder("ticket-assignee", "ticket-plain");
    }

    @Test
    void softDeletedSettingsStillEnforceTheirVisibilityContent() {
        // 软删除设置引用：按其内容判定（与 findByUid/详情口径一致），不静默回退默认
        TicketRequest request = buildRequest(USER_DEPT_ALLOWED, "member-dept-allowed", DEPT_KEFU);
        List<String> uids = queryUids(request);
        assertThat(uids).contains("ticket-softdeleted");

        TicketRequest denied = buildRequest(USER_VIEWER, "member-viewer", DEPT_OTHER);
        assertThat(queryUids(denied)).doesNotContain("ticket-softdeleted");
    }

    @Test
    void underscoreUidAssigneeExemptionAndFilterStillMatch() {
        // 回归 2026-10-15 主诉：member uid 含下划线（df_mb_uid）时，
        // escapeLike 曾双重转义导致 LIKE 匹配 0 行 → 豁免/待我处理全部失效。
        // 场景与现场一致：DEPARTMENT_BASED(仅 dept-kefu)，处理人在 dept-other

        // 1. 受理人豁免（assigneeSelf）：混合列表可见自己被分配的工单
        TicketRequest request = buildRequest(USER_ASSIGNEE, MEMBER_ASSIGNEE, DEPT_OTHER);
        assertThat(queryUids(request)).contains("ticket-assignee");

        // 2. 单类型 EXTERNAL 列表同样命中豁免
        TicketRequest externalOnly = buildRequest(USER_ASSIGNEE, MEMBER_ASSIGNEE, DEPT_OTHER);
        externalOnly.setType(TicketTypeEnum.EXTERNAL.name());
        assertThat(queryUids(externalOnly)).contains("ticket-assignee");

        // 3. assigneeUid 过滤（desktop「待我处理」）：含下划线的 uid 精确命中，不误配其它工单
        TicketRequest assignedToMe = buildRequest(USER_ASSIGNEE, MEMBER_ASSIGNEE, DEPT_OTHER);
        assignedToMe.setAssigneeUid(MEMBER_ASSIGNEE);
        assertThat(queryUids(assignedToMe)).containsExactly("ticket-assignee");

        // 4. userUid 豁免（reporterSelf）不受影响
        TicketRequest asReporter = buildRequest("user-reporter-plain", "member-other", DEPT_OTHER);
        assertThat(queryUids(asReporter)).contains("ticket-plain");

        // 5. 下划线不得变成通配符：df_mb_uid 不能匹配 dfXmbXuid 等变体
        entityManager.persist(TicketEntity.builder()
                .uid("ticket-underscore-wildcard")
                .orgUid(ORG)
                .type(TicketTypeEnum.EXTERNAL.name())
                .status("ASSIGNED")
                .ticketSettingsUid(settingsStrictUid)
                .userUid("user-reporter-wildcard")
                .assignee(compactAssigneeJson("dfXmbXuid"))
                .deleted(false)
                .build());
        entityManager.flush();
        TicketRequest wildcardProbe = buildRequest(USER_ASSIGNEE, MEMBER_ASSIGNEE, DEPT_OTHER);
        assertThat(queryUids(wildcardProbe)).doesNotContain("ticket-underscore-wildcard");
    }
}
