package com.bytedesk.core.sms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.constant.I18Consts;

class SmsSendServiceTest {

    private final SmsSendService smsSendService = new SmsSendService();

    // @Test
    // void shouldReturnFriendlyMessageForDisabledAccessKey() {
    //     String message = smsSendService.resolveAliyunErrorMessage("InvalidAccessKeyId.Inactive",
    //             "Specified access key is disabled.");

    //     // assertEquals(I18Consts.I18N_SMS_SERVICE_CONFIG_ERROR, message);
    //     assertTrue(smsSendService.isAliyunCredentialOrPermissionError("InvalidAccessKeyId.Inactive"));
    // }

    @Test
    void shouldKeepFriendlyValidationMessageForIllegalMobileNumber() {
        String message = smsSendService.resolveAliyunErrorMessage("isv.MOBILE_NUMBER_ILLEGAL", "手机号码格式错误");

        assertEquals("手机号码格式错误", message);
    }

    @Test
    void shouldFallbackToGenericUnavailableMessageForUnknownAliyunErrors() {
        String message = smsSendService.resolveAliyunErrorMessage("UNKNOWN_ERROR", "provider internal error");

        assertEquals(I18Consts.I18N_SMS_SERVICE_UNAVAILABLE, message);
    }

    @Test
    void shouldReturnSuccessForAdminMobileEvenWhenNotWhitelisted() {
        BytedeskProperties bytedeskProperties = mock(BytedeskProperties.class);
        SmsSendService service = new SmsSendService();
        ReflectionTestUtils.setField(service, "bytedeskProperties", bytedeskProperties);

        when(bytedeskProperties.isAdminIdentifier("13345678000")).thenReturn(true);
        when(bytedeskProperties.isInWhitelist("13345678000")).thenReturn(false);

        SmsSendResult result = service.sendSmsWithResult("13345678000", null, "123456", null);

        assertTrue(result.isSuccess());
    }
}