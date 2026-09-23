package com.bytedesk.ai.robot.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bytedesk.core.menu.MenuEntity;
import com.bytedesk.core.menu.MenuRepository;
import com.bytedesk.core.menu.MenuTypeEnum;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRestService;

/**
 * {@link RobotEntitlementService} 单元测试：
 * 验证 AI 权益门控的判定口径与后台菜单可见性一致——
 * /ai 菜单 enabled != false 且 组织 vipLevel >= 菜单 vipLevel 才放行；
 * 菜单缺失 fail-open；组织行优先回退平台行。
 */
@ExtendWith(MockitoExtension.class)
class RobotEntitlementServiceTest {

    private static final String ORG_UID = "org-uid-001";
    private static final String AI_MENU_LINK = "/ai";
    private static final String ADMIN_TYPE = MenuTypeEnum.ADMIN.name();

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrganizationRestService organizationRestService;

    private RobotEntitlementService robotEntitlementService;

    @BeforeEach
    void setUp() {
        robotEntitlementService = new RobotEntitlementService(menuRepository, organizationRestService);
    }

    private void mockOrg(int vipLevel) {
        OrganizationEntity org = OrganizationEntity.builder().vipLevel(vipLevel).build();
        lenient().when(organizationRestService.findFreshByUid(ORG_UID)).thenReturn(Optional.of(org));
    }

    private MenuEntity buildMenu(Integer vipLevel, Boolean enabled) {
        return MenuEntity.builder()
                .link(AI_MENU_LINK)
                .type(ADMIN_TYPE)
                .vipLevel(vipLevel)
                .enabled(enabled)
                .build();
    }

    private void mockPlatformMenu(MenuEntity menu) {
        lenient().when(menuRepository.findByLinkAndOrgUidIsNullAndTypeAndDeletedFalse(AI_MENU_LINK, ADMIN_TYPE))
                .thenReturn(Optional.ofNullable(menu));
    }

    private void mockOrgMenu(MenuEntity menu) {
        lenient().when(menuRepository.findByLinkAndOrgUidAndTypeAndDeletedFalse(AI_MENU_LINK, ORG_UID, ADMIN_TYPE))
                .thenReturn(Optional.ofNullable(menu));
    }

    @Test
    void shouldAllowWhenOrgVipSufficientAndMenuEnabled() {
        mockOrg(2);
        mockOrgMenu(null);
        mockPlatformMenu(buildMenu(1, true));

        RobotEntitlementResult result = robotEntitlementService.evaluate(ORG_UID);

        assertTrue(result.allowed());
        assertEquals(RobotEntitlementResult.Reason.OK, result.reason());
        assertEquals(2, result.orgVipLevel());
        assertEquals(1, result.menuVipLevel());
        assertTrue(result.menuEnabled());
        assertTrue(result.menuFound());
        assertFalse(result.usedOrgMenu());
        assertTrue(robotEntitlementService.isAllowed(ORG_UID));
    }

    @Test
    void shouldAllowWhenVipLevelsEqual() {
        // 边界：组织等级恰好等于菜单要求等级，应放行（>= 语义）
        mockOrg(1);
        mockOrgMenu(null);
        mockPlatformMenu(buildMenu(1, true));

        assertTrue(robotEntitlementService.evaluate(ORG_UID).allowed());
    }

    @Test
    void shouldDenyWhenOrgVipInsufficient() {
        mockOrg(0);
        mockOrgMenu(null);
        mockPlatformMenu(buildMenu(1, true));

        RobotEntitlementResult result = robotEntitlementService.evaluate(ORG_UID);

        assertFalse(result.allowed());
        assertEquals(RobotEntitlementResult.Reason.VIP_LEVEL_INSUFFICIENT, result.reason());
        assertEquals(0, result.orgVipLevel());
        assertEquals(1, result.menuVipLevel());
        assertFalse(robotEntitlementService.isAllowed(ORG_UID));
    }

    @Test
    void shouldDenyWhenMenuDisabledEvenIfVipSufficient() {
        mockOrg(9);
        mockOrgMenu(null);
        mockPlatformMenu(buildMenu(1, false));

        RobotEntitlementResult result = robotEntitlementService.evaluate(ORG_UID);

        assertFalse(result.allowed());
        assertEquals(RobotEntitlementResult.Reason.MENU_DISABLED, result.reason());
        assertFalse(result.menuEnabled());
    }

    @Test
    void shouldFailOpenWhenMenuNotFound() {
        // 异常场景：种子数据未初始化，菜单缺失时 fail-open 放行
        mockOrg(0);
        mockOrgMenu(null);
        mockPlatformMenu(null);

        RobotEntitlementResult result = robotEntitlementService.evaluate(ORG_UID);

        assertTrue(result.allowed());
        assertEquals(RobotEntitlementResult.Reason.MENU_NOT_FOUND, result.reason());
        assertFalse(result.menuFound());
    }

    @Test
    void shouldPreferOrgMenuOverPlatformMenu() {
        // 组织自定义 /ai 菜单行覆盖平台行：组织行 enabled=false 时即使平台行可用也应拒绝
        mockOrg(5);
        mockOrgMenu(buildMenu(1, false));
        mockPlatformMenu(buildMenu(0, true));

        RobotEntitlementResult result = robotEntitlementService.evaluate(ORG_UID);

        assertFalse(result.allowed());
        assertEquals(RobotEntitlementResult.Reason.MENU_DISABLED, result.reason());
        assertTrue(result.usedOrgMenu());
    }

    @Test
    void shouldFallBackToPlatformMenuWhenOrgMenuAbsent() {
        // 无组织行时回退平台行
        mockOrg(1);
        mockOrgMenu(null);
        mockPlatformMenu(buildMenu(1, true));

        RobotEntitlementResult result = robotEntitlementService.evaluate(ORG_UID);

        assertTrue(result.allowed());
        assertFalse(result.usedOrgMenu());
    }

    @Test
    void shouldTreatOrgNotFoundAsVipZero() {
        // 组织不存在：按 vipLevel=0 处理，若菜单要求 vip>=1 则拒绝
        lenient().when(organizationRestService.findFreshByUid(ORG_UID)).thenReturn(Optional.empty());
        mockOrgMenu(null);
        mockPlatformMenu(buildMenu(1, true));

        RobotEntitlementResult result = robotEntitlementService.evaluate(ORG_UID);

        assertFalse(result.allowed());
        assertEquals(RobotEntitlementResult.Reason.VIP_LEVEL_INSUFFICIENT, result.reason());
        assertEquals(0, result.orgVipLevel());
    }

    @Test
    void shouldTreatNullVipLevelsAsZero() {
        // 实体字段为 null（防御）：组织 vip 与菜单 vip 均按 0 处理，0>=0 放行
        lenient().when(organizationRestService.findFreshByUid(ORG_UID))
                .thenReturn(Optional.of(OrganizationEntity.builder().vipLevel(null).build()));
        mockOrgMenu(null);
        mockPlatformMenu(buildMenu(null, null));

        RobotEntitlementResult result = robotEntitlementService.evaluate(ORG_UID);

        assertTrue(result.allowed());
        assertEquals(0, result.orgVipLevel());
        assertEquals(0, result.menuVipLevel());
        assertTrue(result.menuEnabled());
    }

    @Test
    void shouldSkipOrgMenuQueryWhenOrgUidBlank() {
        // orgUid 为空时不查组织行、组织按 0 处理，仅依据平台行判定
        when(menuRepository.findByLinkAndOrgUidIsNullAndTypeAndDeletedFalse(AI_MENU_LINK, ADMIN_TYPE))
                .thenReturn(Optional.of(buildMenu(0, true)));

        RobotEntitlementResult result = robotEntitlementService.evaluate("");

        assertTrue(result.allowed());
        assertFalse(result.usedOrgMenu());
        assertEquals(0, result.orgVipLevel());
        // 未提供组织 uid 时不应触发组织查询
        org.mockito.Mockito.verify(organizationRestService, org.mockito.Mockito.never())
                .findFreshByUid(eq(ORG_UID));
    }
}
