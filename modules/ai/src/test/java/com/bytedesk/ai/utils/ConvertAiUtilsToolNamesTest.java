package com.bytedesk.ai.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.annotation.Tool;

import com.bytedesk.ai.robot_tool.RobotToolConfig;

/**
 * 发布态 toolConfigs → llm.tools 的运行时工具名解析。
 *
 * <p>回归背景：注册表 CLASS 绑定工具 {@code @Tool(name="order_query_my_orders")}，
 * {@code methodName=orderQueryMyOrders}。此前直接返回 methodName，与 Spring AI
 * ToolCallback 定义名不同口径，导致 {@code RobotToolCallbackResolverImpl} 精确匹配失败、
 * 工具被静默丢弃，模型收不到 tools。</p>
 */
class ConvertAiUtilsToolNamesTest {

    static class SampleOrderTools {

        @Tool(name = "order_query_my_orders", description = "query my orders")
        public String orderQueryMyOrders() {
            return "ok";
        }

        @Tool(description = "no explicit name, falls back to method name")
        public String orderPing() {
            return "pong";
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> resolveToolNames(RobotToolConfig config) throws Exception {
        Method method = ConvertAiUtils.class.getDeclaredMethod("resolveToolNames", RobotToolConfig.class);
        method.setAccessible(true);
        return (List<String>) method.invoke(null, config);
    }

    @Test
    void shouldPreferRegistryRuntimeNameOverMethodNameForClassBinding() throws Exception {
        RobotToolConfig config = RobotToolConfig.builder()
                .key("orderTools.orderQueryMyOrders")
                .name("order_query_my_orders")
                .bindingType("CLASS")
                .beanName("orderTools")
                .className(SampleOrderTools.class.getName())
                .methodName("orderQueryMyOrders")
                .enabled(true)
                .build();

        assertThat(resolveToolNames(config)).containsExactly("order_query_my_orders");
    }

    @Test
    void shouldFallBackToMethodNameWhenRegistryNameMissing() throws Exception {
        RobotToolConfig config = RobotToolConfig.builder()
                .bindingType("CLASS")
                .methodName("orderQueryMyOrders")
                .enabled(true)
                .build();

        assertThat(resolveToolNames(config)).containsExactly("orderQueryMyOrders");
    }

    @Test
    void shouldScanToolAnnotationNamesWhenMethodNameMissing() throws Exception {
        RobotToolConfig config = RobotToolConfig.builder()
                .bindingType("CLASS")
                .className(SampleOrderTools.class.getName())
                .enabled(true)
                .build();

        // 类扫描分支也必须取 @Tool.name（缺省才用方法名），不能一律用 Java 方法名
        assertThat(resolveToolNames(config))
                .containsExactlyInAnyOrder("order_query_my_orders", "orderPing");
    }

    @Test
    void shouldPreferRegistryNameForBeanBinding() throws Exception {
        RobotToolConfig config = RobotToolConfig.builder()
                .name("order_query_my_orders")
                .bindingType("SPRING_BEAN")
                .beanName("orderQueryMyOrdersToolCallback")
                .enabled(true)
                .build();

        assertThat(resolveToolNames(config)).containsExactly("order_query_my_orders");
    }

    @Test
    void shouldKeepBeanNameFallbackForCustomBeanBindingWithoutName() throws Exception {
        RobotToolConfig config = RobotToolConfig.builder()
                .bindingType("FUNCTION_BEAN")
                .beanName("myCustomToolCallback")
                .enabled(true)
                .build();

        assertThat(resolveToolNames(config)).containsExactly("myCustomToolCallback");
    }

    @Test
    void shouldSkipDisabledToolConfig() throws Exception {
        RobotToolConfig config = RobotToolConfig.builder()
                .name("order_query_my_orders")
                .bindingType("CLASS")
                .enabled(false)
                .build();

        assertThat(resolveToolNames(config)).isEmpty();
    }
}
