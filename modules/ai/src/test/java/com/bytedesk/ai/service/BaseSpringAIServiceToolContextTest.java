package com.bytedesk.ai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;

import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.tool.utils.RobotToolCallbackResolver;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.core.thread.ThreadProtobuf;

/**
 * applyRobotToolCallbacks 写入 toolContext 的请求归属键验证。
 *
 * <p>普通 Spring AI 回调链路只读 toolContext（不读 provider-native 的 ThreadLocal holder），
 * 受控订单/物流工具依赖 {@code orgUid} 判定作用域、依赖 {@code threadUid} 写会话绑定。
 * 这里用真实 options 走完整注入链路，断言 DefaultToolCallingManager 实际会传给工具的 map。</p>
 */
class BaseSpringAIServiceToolContextTest {

    private static final String ORG_UID = "df_org_uid";

    private static final String ROBOT_UID = "df_rt_uid";

    private static final String THREAD_UID = "th_visitor_001";

    private static final String MESSAGE_UID = "msg_001";

    private TestService service;

    @BeforeEach
    void setUp() {
        service = new TestService();
        service.robotToolCallbackResolver = new StubResolver();
    }

    private static RobotLlm llm() {
        return RobotLlm.builder()
                .textProvider("openai")
                .textModel("gpt-4o-mini")
                .tools(List.of("order_query_my_orders"))
                .build();
    }

    private static MessageProtobuf visitorMessage(String topic) {
        return MessageProtobuf.builder()
                .uid(MESSAGE_UID)
                .thread(ThreadProtobuf.builder().uid(THREAD_UID).topic(topic).build())
                .build();
    }

    private static RobotProtobuf robot() {
        return RobotProtobuf.builder().uid(ROBOT_UID).orgUid(ORG_UID).llm(llm()).build();
    }

    @Test
    void shouldCarryRequestOwnershipAndVisitorUidForCustomerServiceTopic() {
        MessageProtobuf message = visitorMessage("org/robot/" + ROBOT_UID + "/visitor_001");

        ToolCallingChatOptions options = service.apply(
                OpenAiChatOptions.builder().model("gpt-4o-mini").build(), llm(), robot(), message);

        Map<String, Object> context = options.getToolContext();
        assertThat(context)
                .containsEntry("orgUid", ORG_UID)
                .containsEntry("robotUid", ROBOT_UID)
                .containsEntry("threadUid", THREAD_UID)
                .containsEntry("messageUid", MESSAGE_UID)
                .containsEntry("visitorUid", "visitor_001");
    }

    @Test
    void shouldNotInferVisitorUidFromAdminLlmTopicButStillCarryOrgAndThread() {
        // 管理端 LLM 会话为 5 段 topic，第 3 段是坐席 uid，不得被当作访客身份
        MessageProtobuf message = visitorMessage("org/robot/" + ROBOT_UID + "/member_1/abc123");

        ToolCallingChatOptions options = service.apply(
                OpenAiChatOptions.builder().model("gpt-4o-mini").build(), llm(), robot(), message);

        Map<String, Object> context = options.getToolContext();
        assertThat(context)
                .containsEntry("orgUid", ORG_UID)
                .containsEntry("threadUid", THREAD_UID)
                .doesNotContainKey("visitorUid");
    }

    @Test
    void shouldFailClosedWithoutOrgUidWhenRobotOrgMissing() {
        RobotProtobuf orglessRobot = RobotProtobuf.builder().uid(ROBOT_UID).llm(llm()).build();
        MessageProtobuf message = visitorMessage("org/robot/" + ROBOT_UID + "/visitor_001");

        ToolCallingChatOptions options = service.apply(
                OpenAiChatOptions.builder().model("gpt-4o-mini").build(), llm(), orglessRobot, message);

        // robot.orgUid 缺失时不得回退任何默认组织；visitorUid 候选也依赖 robot.orgUid，一并不注入
        assertThat(options.getToolContext())
                .doesNotContainKey("orgUid")
                .doesNotContainKey("visitorUid")
                .containsEntry("threadUid", THREAD_UID);
    }

    @Test
    void shouldKeepToolCallbacksAndNotTouchContextWhenNoToolsConfigured() {
        // 解析器返回空（无可用工具）时直接返回原 options，不注入回调、不写上下文，普通对话行为保持不变
        service.robotToolCallbackResolver = requested -> List.of();
        RobotLlm toolless = RobotLlm.builder().textProvider("openai").textModel("gpt-4o-mini").build();
        RobotProtobuf robot = RobotProtobuf.builder().uid(ROBOT_UID).orgUid(ORG_UID).llm(toolless).build();
        OpenAiChatOptions original = OpenAiChatOptions.builder().model("gpt-4o-mini").build();

        OpenAiChatOptions result = service.apply(original, toolless, robot, visitorMessage("org/robot/x/visitor_001"));

        assertThat(result).isSameAs(original);
        assertThat(result.getToolCallbacks()).isNullOrEmpty();
        assertThat(result.getToolContext()).isNullOrEmpty();
    }

    @Test
    void shouldNotDuplicateToolCallbacksInDefaultOptionsWhenPromptCarriesTools() {
        // 回归：Spring AI 合并 ChatOptions 时对 toolCallbacks 是 addAll 拼接而非覆盖。
        // 若 createChatClient 把带工具的 prompt options 原样再塞进 defaultOptions，
        // 同一工具会被发送两份，DeepSeek 直接 400 "Tool names must be unique"。
        OpenAiChatOptions promptOptions = service.apply(
                OpenAiChatOptions.builder().model("gpt-4o-mini").build(), llm(), robot(),
                visitorMessage("org/robot/" + ROBOT_UID + "/visitor_001"));
        assertThat(promptOptions.getToolCallbacks()).hasSize(1);

        org.springframework.ai.chat.prompt.Prompt prompt =
                new org.springframework.ai.chat.prompt.Prompt(List.of(new org.springframework.ai.chat.messages.UserMessage("hi")),
                        promptOptions);

        // 捕获 ChatClient 合并后交给 ToolCallingManager 的 options（即真正发给模型的工具集）
        java.util.List<String> mergedNames = new java.util.ArrayList<>();
        org.springframework.ai.model.tool.ToolCallingManager spyManager =
                new org.springframework.ai.model.tool.ToolCallingManager() {
                    @Override
                    public java.util.List<org.springframework.ai.tool.definition.ToolDefinition> resolveToolDefinitions(
                            ToolCallingChatOptions opts) {
                        if (opts != null && opts.getToolCallbacks() != null) {
                            opts.getToolCallbacks().forEach(c -> mergedNames.add(c.getToolDefinition().name()));
                        }
                        throw new IllegalStateException("SPY_STOP");
                    }

                    @Override
                    public org.springframework.ai.model.tool.ToolExecutionResult executeToolCalls(
                            org.springframework.ai.chat.prompt.Prompt p,
                            org.springframework.ai.chat.model.ChatResponse r) {
                        throw new UnsupportedOperationException();
                    }
                };
        // 用 DeepSeekChatModel 作为被测 ChatModel（测试类路径已有；spy 会先抛出，不发网络请求）
        org.springframework.ai.deepseek.DeepSeekChatModel spyModel = org.springframework.ai.deepseek.DeepSeekChatModel
                .builder()
                .deepSeekApi(org.springframework.ai.deepseek.api.DeepSeekApi.builder()
                        .apiKey("test").baseUrl("http://localhost:1").build())
                .toolCallingManager(spyManager)
                .build();

        org.springframework.ai.chat.client.ChatClient chatClient = service.createChatClient(spyModel, prompt, null);

        assertThatThrownBy(() -> chatClient.prompt(prompt).call().content())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("SPY_STOP");

        assertThat(mergedNames).containsExactly("order_query_my_orders");
    }

    /** 只暴露被测方法的极简具体子类（无参构造，不装配任何 provider 依赖）。 */
    static class TestService extends BaseSpringAIService {

        <T extends ToolCallingChatOptions> T apply(T options, RobotLlm llm, RobotProtobuf robot,
                MessageProtobuf message) {
            return applyRobotToolCallbacks(options, llm, robot, message);
        }

        @Override
        protected void processPromptSse(org.springframework.ai.chat.prompt.Prompt prompt, RobotProtobuf robot,
                MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
                List<RobotContent.SourceReference> sourceReferences,
                org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter) {
            throw new UnsupportedOperationException("not used in this test");
        }

        @Override
        protected String processPromptSync(String message, RobotProtobuf robot) {
            throw new UnsupportedOperationException("not used in this test");
        }
    }

    /** 返回一个占位回调，使 resolveToolCallbacks 非空从而走到 toolContext 注入分支。 */
    static class StubResolver implements RobotToolCallbackResolver {

        @Override
        public List<ToolCallback> resolveToolCallbacks(List<String> requestedToolNames) {
            return List.of(new StubToolCallback("order_query_my_orders"));
        }
    }

    record StubToolCallback(String name) implements ToolCallback {

        @Override
        public ToolDefinition getToolDefinition() {
            return ToolDefinition.builder().name(name).description("stub").inputSchema("{}").build();
        }

        @Override
        public String call(String toolInput) {
            return "{}";
        }

        @Override
        public String call(String toolInput, ToolContext toolContext) {
            return "{}";
        }
    }
}
