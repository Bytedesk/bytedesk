package com.bytedesk.service.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.modelmapper.ModelMapper;

import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.service.visitor.VisitorProtobuf;
import com.bytedesk.service.visitor.VisitorRequest;

/**
 * thread_user 写入护栏（2026-09-14 规划 P1-2.3）防回归测试：
 * 超过 32KB 的访客 JSON 落库前剥离 extra 字段；剥离后仍超限则截断——
 * 保证 INSERT 不再因 TEXT(64KB) 列超长失败（列宽维持不变，见规划 D1 决策）。
 */
class ServiceConvertUtilsVisitorJsonGuardTest {

    @BeforeAll
    static void setUp() throws Exception {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBean(ModelMapper.class)).thenReturn(new ModelMapper());
        Field contextField = ApplicationContextHolder.class.getDeclaredField("context");
        contextField.setAccessible(true);
        contextField.set(null, applicationContext);
    }

    @Test
    void normalVisitorJsonPassesThroughUnchanged() {
        VisitorRequest request = VisitorRequest.builder()
                .uid("visitor-1")
                .nickname("访客")
                .build();
        String json = ServiceConvertUtils.convertToVisitorProtobufJSONString(request);
        // 正常大小 JSON 原样通过（护栏不介入）
        org.junit.jupiter.api.Assertions.assertTrue(json.contains("visitor-1"));
        org.junit.jupiter.api.Assertions.assertTrue(json.length() < 32 * 1024);
    }

    @Test
    void oversizedExtraIsStrippedBeforePersist() {
        String hugeExtra = "x".repeat(40 * 1024);
        VisitorRequest request = VisitorRequest.builder()
                .uid("visitor-1")
                .nickname("访客")
                .extra(hugeExtra)
                .build();
        String json = ServiceConvertUtils.convertToVisitorProtobufJSONString(request);
        // 剥离 extra 后小于阈值
        org.junit.jupiter.api.Assertions.assertTrue(json.length() <= 32 * 1024,
                "json length should be guarded, actual=" + json.length());
        // extra 内容不再写入
        org.junit.jupiter.api.Assertions.assertFalse(json.contains(hugeExtra));
        // 核心字段保留（可反序列化）
        VisitorProtobuf parsed = VisitorProtobuf.fromJson(json);
        org.junit.jupiter.api.Assertions.assertEquals("visitor-1", parsed.getUid());
        org.junit.jupiter.api.Assertions.assertNull(parsed.getExtra());
    }
}
