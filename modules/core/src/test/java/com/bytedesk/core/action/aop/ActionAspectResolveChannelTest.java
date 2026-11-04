package com.bytedesk.core.action.aop;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.rbac.auth.AuthRequest;

/**
 * ActionAspect 渠道解析：登录通知等场景需要从请求体中提取 channel。
 */
@ExtendWith(MockitoExtension.class)
class ActionAspectResolveChannelTest {

    @Mock
    private com.bytedesk.core.action.ActionRestService actionRestService;

    @Mock
    private IpService ipService;

    @InjectMocks
    private ActionAspect actionAspect;

    @Test
    void resolvesChannelFromBaseRequestBody() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setChannel("WEB_ADMIN");

        assertThat(actionAspect.resolveChannel(new Object[] { authRequest, null })).isEqualTo("WEB_ADMIN");
    }

    @Test
    void trimsChannelWhitespace() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setChannel("  WEB_ADMIN  ");

        assertThat(actionAspect.resolveChannel(new Object[] { authRequest })).isEqualTo("WEB_ADMIN");
    }

    @Test
    void blankChannelReturnsNull() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setChannel("");

        assertThat(actionAspect.resolveChannel(new Object[] { authRequest })).isNull();
    }

    @Test
    void resolvesChannelFromNonBaseRequestDto() {
        LoginDto dto = new LoginDto();
        dto.setChannel("UNIAPP_ANDROID");

        assertThat(actionAspect.resolveChannel(new Object[] { dto })).isEqualTo("UNIAPP_ANDROID");
    }

    @Test
    void dtoWithoutChannelReturnsNull() {
        NoChannelDto dto = new NoChannelDto();

        assertThat(actionAspect.resolveChannel(new Object[] { dto })).isNull();
    }

    @Test
    void nullArgsReturnsNull() {
        assertThat(actionAspect.resolveChannel(null)).isNull();
    }

    @Test
    void skipsNullAndServletRequestArgs() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setChannel("WEB_OFFICE");

        assertThat(actionAspect.resolveChannel(new Object[] { null, "raw-string", authRequest }))
                .isEqualTo("WEB_OFFICE");
    }

    // 未继承 BaseRequest 但含 channel 字段的请求 DTO（模拟 OAuth 登录请求体）
    @lombok.Data
    static class LoginDto {
        private String channel;
    }

    @lombok.Data
    static class NoChannelDto {
        private String code;
    }

}
