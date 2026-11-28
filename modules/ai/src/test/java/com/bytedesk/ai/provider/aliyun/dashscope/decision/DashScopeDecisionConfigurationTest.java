package com.bytedesk.ai.provider.aliyun.dashscope.decision;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.ai.llm_provider.LlmProviderRestService;
import com.bytedesk.core.config.properties.BytedeskProperties;

/**
 * 验证 {@code spring.ai.dashscope.decision.*} 属性绑定与 Controller 的 enabled 条件装配。
 */
class DashScopeDecisionConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withInitializer(context -> {
                // 以 singleton 方式注册 mock，避免 Spring 对 Mockito mock 做 @Autowired 注入
                // （BaseRestService.setAuthService 会要求 AuthService bean）
                context.getBeanFactory().registerSingleton("llmProviderRestService",
                        mock(LlmProviderRestService.class));
            })
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                    "spring.ai.dashscope.api-key=sk-global",
                    "spring.ai.dashscope.base-url=https://dashscope.aliyuncs.com");

    @Test
    void bindsPropertiesAndEnablesControllerByDefault() {
        runner.withPropertyValues(
                "spring.ai.dashscope.decision.model=decision-model-preview",
                "spring.ai.dashscope.decision.workspace-id=llm-abc",
                "spring.ai.dashscope.decision.region=ap-southeast-1",
                "spring.ai.dashscope.decision.timeout=15s")
                .run(context -> {
                    assertThat(context).hasSingleBean(DashScopeDecisionProperties.class);
                    DashScopeDecisionProperties properties = context.getBean(DashScopeDecisionProperties.class);
                    assertThat(properties.getEnabled()).isTrue();
                    assertThat(properties.getModel()).isEqualTo("decision-model-preview");
                    assertThat(properties.getWorkspaceId()).isEqualTo("llm-abc");
                    assertThat(properties.getRegion()).isEqualTo("ap-southeast-1");
                    assertThat(properties.getTimeout()).isEqualTo(Duration.ofSeconds(15));

                    assertThat(context).hasSingleBean(DashScopeDecisionService.class);
                    // workspace-id 优先于 base-url 回退
                    assertThat(context.getBean(DashScopeDecisionService.class).resolveEndpoint(null))
                            .isEqualTo("https://llm-abc.ap-southeast-1.maas.aliyuncs.com/compatible-mode/v1");
                    assertThat(context).hasSingleBean(DashScopeDecisionController.class);
                });
    }

    @Test
    void fallsBackToDashScopeBaseUrlWhenNoDecisionSpecificConfig() {
        runner.run(context -> {
            DashScopeDecisionProperties properties = context.getBean(DashScopeDecisionProperties.class);
            assertThat(properties.getModel()).isEqualTo("decision-model");
            assertThat(properties.getTimeout()).isEqualTo(Duration.ofSeconds(60));
            assertThat(context.getBean(DashScopeDecisionService.class).resolveEndpoint(null))
                    .isEqualTo("https://dashscope.aliyuncs.com/compatible-mode/v1");
        });
    }

    @Test
    void disabledFlagRemovesControllerButKeepsServiceAvailable() {
        runner.withPropertyValues("spring.ai.dashscope.decision.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(DashScopeDecisionController.class);
            assertThat(context).hasSingleBean(DashScopeDecisionService.class);
        });
    }

    /**
     * 只扫描 decision 包：让 {@code @Service} / {@code @RestController} / {@code @ConfigurationProperties}
     * 以与生产一致的方式注册，从而使 {@code @ConditionalOnProperty} 等原型注解真正生效。
     */
    @Configuration
    @ComponentScan(basePackageClasses = DashScopeDecisionService.class)
    static class TestConfig {

        @Bean
        BytedeskProperties bytedeskProperties() {
            return new BytedeskProperties();
        }
    }
}
