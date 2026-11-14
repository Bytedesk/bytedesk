package com.bytedesk.ai.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.bytedesk.core.constant.I18Consts;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volcengine.ark.runtime.exception.ArkAPIError;
import com.volcengine.ark.runtime.exception.ArkHttpException;

/**
 * SseMessageHelper.resolveErrorText 错误信息透传验证。
 */
class SseMessageHelperErrorTextTest {

    @Test
    void shouldReturnMostSpecificCauseMessage() {
        // 模拟包装异常：外层 IllegalStateException，内层为供应商真实错误
        IllegalStateException wrapped = new IllegalStateException("Volcengine Ark chat stream failed",
                new RuntimeException("The model or endpoint doubao-x does not exist. code=InvalidEndpointOrModel.NotFound"));

        String text = SseMessageHelper.resolveErrorText(wrapped);

        assertTrue(text.contains("doubao-x"));
        assertTrue(text.contains("InvalidEndpointOrModel.NotFound"));
    }

    @Test
    void shouldSkipStatusOnlyCauseAndUseArkExceptionDetail() {
        // 复现线上场景：ArkHttpException 的 cause 是 retrofit2.HttpException，
        // message 仅为 "HTTP 404 "，真实错误在外层 ArkHttpException 中
        String errorBody = "{\"error\":{\"message\":\"Your account 2102212660 has not activated the model "
                + "glm-5-3-flash-260828. Please activate the model service in the Ark Console. "
                + "Request id: 021794359339584d834e9561d4e0ec1726929f79edd562aa6f571\","
                + "\"type\":\"Not Found\",\"param\":\"\",\"code\":\"ModelNotOpen\"}}";
        ArkAPIError apiError = ArkAPIError.fromResponseBody(new ObjectMapper(), errorBody, "HTTP 404 ");
        ArkHttpException arkException = new ArkHttpException(apiError,
                new RuntimeException("HTTP 404 "), 404, "021794359339584d834e9561d4e0ec1726929f79edd562aa6f571");

        String text = SseMessageHelper.resolveErrorText(arkException);

        assertTrue(text.startsWith("[ModelNotOpen] Your account 2102212660"));
        assertTrue(text.contains("glm-5-3-flash-260828"));
        assertTrue(text.contains("021794359339584d834e9561d4e0ec1726929f79edd562aa6f571"));
        assertFalse(text.contains("ArkHttpException{"));
        assertFalse(text.contains("HTTP 404"));
    }

    @Test
    void shouldSkipBareStatusCodeMessage() {
        RuntimeException statusOnly = new RuntimeException("HTTP 503 ");

        String text = SseMessageHelper.resolveErrorText(new IllegalStateException("upstream call failed", statusOnly));

        assertEquals("upstream call failed", text);
    }

    @Test
    void shouldReturnExceptionClassNameWhenMessageMissing() {
        RuntimeException noMessage = new RuntimeException();

        String text = SseMessageHelper.resolveErrorText(noMessage);

        assertEquals("RuntimeException", text);
    }

    @Test
    void shouldTruncateOverlyLongMessage() {
        String longMessage = "x".repeat(2000);
        RuntimeException longError = new RuntimeException(longMessage);

        String text = SseMessageHelper.resolveErrorText(longError);

        assertEquals(500 + "...".length(), text.length());
        assertTrue(text.endsWith("..."));
    }

    @Test
    void shouldFallbackToGenericTipForNullError() {
        assertEquals(I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE,
                SseMessageHelper.resolveErrorText(null));
    }
}
