package com.bytedesk.ai.robot_settings.tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Declarative definition of a Spring AI tool binding that can be enabled for a
 * robot. The configuration is stored as JSON on RobotToolsSettingsEntity and
 * mirrored to the frontend so that admins can toggle and edit bindings.
 */
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RobotToolConfig implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * Unique identifier for locating and deduplicating a tool (e.g.
         * spring.datetime)
         */
        private String key;

        /**
         * Display name shown to admins when configuring the tool.
         */
        private String name;

        /**
         * Short description of what the tool does.
         */
        private String description;

        /**
         * Visual category (utility / workflow / knowledge etc.)
         */
        private String category;

        /**
         * Optional emoji/icon string for quick visual cues in the UI.
         */
        private String icon;

        /**
         * Whether this tool is enabled for the current robot template.
         */
        @Builder.Default
        private Boolean enabled = true;

        /**
         * Binding type: CLASS, SPRING_BEAN, FUNCTION_BEAN, HTTP_ENDPOINT, CUSTOM,
         * MCP_TOOL, WEB_SEARCH
         */
        private String bindingType;

        /**
         * Spring bean name when bindingType == SPRING_BEAN / FUNCTION_BEAN
         */
        private String beanName;

        /**
         * Fully-qualified class name when bindingType == CLASS
         */
        private String className;

        /**
         * Optional method name for CLASS binding. When omitted all @Tool methods are
         * exposed.
         */
        private String methodName;

        /**
         * External endpoint when bindingType == HTTP_ENDPOINT
         */
        private String endpoint;

        /**
         * Linked MCP server definition when bindingType == MCP_TOOL.
         */
        private String mcpServerUid;

        /**
         * Optional query template used by search-capable tools.
         */
        private String searchQueryTemplate;

        /**
         * Optional cap for search results returned to the model.
         */
        private Integer searchResultLimit;

        /**
         * JSON schema snippet describing the tool input payload.
         */
        private String inputSchema;

        /**
         * JSON schema or textual hint describing the response payload.
         */
        private String outputSchema;

        /**
         * Custom instructions injected before executing the tool.
         */
        private String systemPrompt;

        /**
         * Maximum priority/order used when presenting tools to the LLM.
         */
        private Integer orderIndex;

        /**
         * Whether the tool requires manual approval before execution.
         */
        @Builder.Default
        private Boolean requiresApproval = false;

        private List<String> intentKeywords;

        @Builder.Default
        private String intentMatchMode = ToolIntentMatchMode.KEYWORD.name();

        /**
         * Free-form metadata reserved for provider specific hints.
         */
        @Builder.Default
        private Map<String, Object> metadata = new HashMap<>();

        /**
         * Provide a curated set of builtin Spring AI demo tools so that a brand-new
         * robot template always contains meaningful defaults.
         */
        public static List<RobotToolConfig> defaultSpringAiTools() {
                List<RobotToolConfig> defaults = new ArrayList<>();
                defaults.add(RobotToolConfig.builder()
                                .key("spring.datetime")
                                .name("Date & Time Helper")
                                .description("获取访客所在时区的当前日期与时间")
                                .category("utility")
                                .icon("🕒")
                                .bindingType("CLASS")
                                .className("com.bytedesk.ai.tool.test.DateTimeTools")
                                .methodName("getCurrentDateTime")
                                .inputSchema("{\"type\":\"object\",\"properties\":{}}")
                                .outputSchema("ISO-8601 timestamp string")
                                .intentKeywords(Arrays.asList("现在时间", "当前时间", "几点", "几号", "日期", "今天", "明天", "后天",
                                                "time",
                                                "date", "day", "today", "tomorrow", "current time", "current date",
                                                "what time", "北京时间"))
                                .orderIndex(10)
                                .build());
                defaults.add(RobotToolConfig.builder()
                                .key("spring.math")
                                .name("Math Toolkit")
                                .description("执行加减乘除等常见数学运算")
                                .category("utility")
                                .icon("➗")
                                .bindingType("CLASS")
                                .className("com.bytedesk.ai.tool.test.MathTools")
                                .methodName(null)
                                .inputSchema("See individual @Tool annotations on MathTools")
                                .intentKeywords(Arrays.asList("计算", "算一下", "加", "减", "乘", "除", "等于", "plus", "minus",
                                                "multiply", "multiplied", "times", "divide", "divided", "calculate",
                                                "sum"))
                                .orderIndex(20)
                                .build());
                defaults.add(RobotToolConfig.builder()
                                .key("spring.weather")
                                .name("Weather (Function Bean)")
                                .description("查询指定城市的实时天气。依赖 WeatherTools#currentWeather 函数 bean")
                                .category("data")
                                .icon("☁️")
                                .enabled(false)
                                .bindingType("SPRING_BEAN")
                                .beanName("currentWeather")
                                .inputSchema("{\"type\":\"object\",\"properties\":{\"location\":{\"type\":\"string\"}}}")
                                .intentKeywords(Arrays.asList("天气", "温度", "下雨", "下雪", "晴", "阴", "多云", "weather",
                                                "forecast",
                                                "temperature", "hot", "cold"))
                                .orderIndex(30)
                                .build());
                defaults.add(RobotToolConfig.builder()
                                .key("spring.datetime.alarm")
                                .name("Alarm Planner")
                                .description("允许访客设置未来提醒时间，演示 MethodToolCallback 用法")
                                .category("workflow")
                                .icon("⏰")
                                .bindingType("CLASS")
                                .className("com.bytedesk.ai.tool.test.DateTimeTools")
                                .methodName("setAlarm")
                                .inputSchema("{\"type\":\"object\",\"properties\":{\"time\":{\"type\":\"string\",\"format\":\"date-time\"}}}")
                                .intentKeywords(Arrays.asList("闹钟", "提醒", "定时", "alarm", "remind", "reminder",
                                                "wake me"))
                                .orderIndex(40)
                                .build());
                defaults.add(RobotToolConfig.builder()
                                .key("builtin.web_search")
                                .name("Web Search")
                                .description("查询实时公开信息，适合天气、新闻、价格和最新动态等知识库外问题")
                                .category("knowledge")
                                .icon("🌐")
                                .enabled(false)
                                .bindingType("WEB_SEARCH")
                                .inputSchema("{\"type\":\"object\",\"properties\":{\"query\":{\"type\":\"string\"}}}")
                                .outputSchema("Search snippets or summarized web results")
                                .searchResultLimit(5)
                                .intentKeywords(Arrays.asList("联网搜索", "搜索一下", "帮我查", "最新", "新闻", "天气", "股价",
                                                "实时", "web search", "search online", "look up", "latest news"))
                                .orderIndex(50)
                                .build());
                return defaults;
        }
}
