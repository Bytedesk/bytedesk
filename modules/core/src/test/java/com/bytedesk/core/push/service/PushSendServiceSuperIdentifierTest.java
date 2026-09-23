/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-21
 * @Description: D3 固定验证码超管口径单测（docs/plans/2026-09-21-user-member-contact-sync-plan.md）
 */
package com.bytedesk.core.push.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.email_provider.EmailSendService;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.push.PushFilterService;
import com.bytedesk.core.push.PushRestService;
import com.bytedesk.core.push.strategy.AuthValidationStrategyFactory;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.sms_push.SmsPushSendService;

class PushSendServiceSuperIdentifierTest {

    @Test
    void superAdminIdentifierShouldFollowDatabaseSuperUser() {
        BytedeskProperties properties = mock(BytedeskProperties.class);
        UserService userService = mock(UserService.class);
        PushSendService pushSendService = new PushSendService(
                mock(AuthValidationStrategyFactory.class),
                mock(EmailSendService.class),
                mock(SmsPushSendService.class),
                properties,
                mock(IpService.class),
                mock(PushFilterService.class),
                mock(PushRestService.class),
                userService);

        // 配置残留旧号（超管已改号），DB 超管当前手机号为 13311156272
        when(properties.isAdminIdentifier("13345678000")).thenReturn(false);
        when(properties.isAdminIdentifier("13311156272")).thenReturn(false);
        UserEntity superUser = UserEntity.builder()
                .uid("df_user_super")
                .mobile("13311156272")
                .email("admin@email.com")
                .build();
        when(userService.getSuper()).thenReturn(Optional.of(superUser));

        // DB 超管当前手机号/邮箱 → 命中
        assertTrue(pushSendService.isSuperAdminIdentifier("13311156272"));
        assertTrue(pushSendService.isSuperAdminIdentifier("admin@email.com"));
        // 旧号已不被 DB 超管占用，也不再命中固定验证码
        assertFalse(pushSendService.isSuperAdminIdentifier("13345678000"));
        assertFalse(pushSendService.isSuperAdminIdentifier("13900000000"));
        assertFalse(pushSendService.isSuperAdminIdentifier(null));
        assertFalse(pushSendService.isSuperAdminIdentifier(" "));
    }

    @Test
    void configFallbackStillAppliesWhenNoSuperUserInDatabase() {
        BytedeskProperties properties = mock(BytedeskProperties.class);
        UserService userService = mock(UserService.class);
        PushSendService pushSendService = new PushSendService(
                mock(AuthValidationStrategyFactory.class),
                mock(EmailSendService.class),
                mock(SmsPushSendService.class),
                properties,
                mock(IpService.class),
                mock(PushFilterService.class),
                mock(PushRestService.class),
                userService);

        // 首次启动场景：DB 无超管，配置值作为兜底
        when(properties.isAdminIdentifier("13345678000")).thenReturn(true);
        when(userService.getSuper()).thenReturn(Optional.empty());

        assertTrue(pushSendService.isSuperAdminIdentifier("13345678000"));
        assertFalse(pushSendService.isSuperAdminIdentifier("13311156272"));
    }
}
