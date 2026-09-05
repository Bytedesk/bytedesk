package com.bytedesk.ai.provider.zhipuai;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * {@link ZhipuaiUrlUtils} 单元测试：验证各种用户输入形态的 baseUrl 均能规范化为
 * Retrofit 兼容（以 "/v4/" 结尾）的智谱 API 地址。
 */
class ZhipuaiUrlUtilsTest {

    @Test
    void normalizeShouldHandleOfficialDocUrlWithoutTrailingSlash() {
        // 管理后台常见输入：官方文档地址，无 /v4、无结尾斜杠
        assertEquals("https://open.bigmodel.cn/api/paas/v4/",
                ZhipuaiUrlUtils.normalizeBaseUrl("https://open.bigmodel.cn/api/paas"));
    }

    @Test
    void normalizeShouldHandleTrailingSlashOnly() {
        assertEquals("https://open.bigmodel.cn/api/paas/v4/",
                ZhipuaiUrlUtils.normalizeBaseUrl("https://open.bigmodel.cn/api/paas/"));
    }

    @Test
    void normalizeShouldHandleV4WithoutTrailingSlash() {
        assertEquals("https://open.bigmodel.cn/api/paas/v4/",
                ZhipuaiUrlUtils.normalizeBaseUrl("https://open.bigmodel.cn/api/paas/v4"));
    }

    @Test
    void normalizeShouldKeepAlreadyNormalizedUrl() {
        assertEquals("https://open.bigmodel.cn/api/paas/v4/",
                ZhipuaiUrlUtils.normalizeBaseUrl("https://open.bigmodel.cn/api/paas/v4/"));
    }

    @Test
    void normalizeShouldHandleMultipleTrailingSlashes() {
        assertEquals("https://open.bigmodel.cn/api/paas/v4/",
                ZhipuaiUrlUtils.normalizeBaseUrl("https://open.bigmodel.cn/api/paas///"));
    }

    @Test
    void normalizeShouldTrimWhitespace() {
        assertEquals("https://open.bigmodel.cn/api/paas/v4/",
                ZhipuaiUrlUtils.normalizeBaseUrl("  https://open.bigmodel.cn/api/paas "));
    }

    @Test
    void normalizeShouldHandleZaiDomain() {
        assertEquals("https://api.z.ai/api/paas/v4/",
                ZhipuaiUrlUtils.normalizeBaseUrl("https://api.z.ai/api/paas"));
    }

    @Test
    void normalizeShouldReturnDefaultForBlankInput() {
        assertEquals(ZhipuaiUrlUtils.DEFAULT_ZHIPUAI_BASE_URL,
                ZhipuaiUrlUtils.normalizeBaseUrl(null));
        assertEquals(ZhipuaiUrlUtils.DEFAULT_ZHIPUAI_BASE_URL,
                ZhipuaiUrlUtils.normalizeBaseUrl(""));
        assertEquals(ZhipuaiUrlUtils.DEFAULT_ZHIPUAI_BASE_URL,
                ZhipuaiUrlUtils.normalizeBaseUrl("   "));
    }

    @Test
    void normalizedUrlShouldPassRetrofitBaseUrlCheck() {
        // Retrofit 要求 baseUrl 以 "/" 结尾，且 SDK API 为相对路径，需包含版本段
        String normalized = ZhipuaiUrlUtils.normalizeBaseUrl("https://open.bigmodel.cn/api/paas");
        assertEquals(true, normalized.endsWith("/"));
        assertEquals(true, normalized.contains("/v4/"));
    }
}
