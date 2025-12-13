package com.bytedesk.ai.robot_settings.tools;

import java.io.Serializable;
import java.util.ArrayList;
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
     * Unique identifier for locating and deduplicating a tool (e.g. spring.datetime)
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
     * Binding type: CLASS, SPRING_BEAN, FUNCTION_BEAN, HTTP_ENDPOINT, CUSTOM
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
     * Optional method name for CLASS binding. When omitted all @Tool methods are exposed.
     */
    private String methodName;

    /**
     * External endpoint when bindingType == HTTP_ENDPOINT
     */
    private String endpoint;

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
                .className("com.bytedesk.ai.utils.tools.DateTimeTools")
                .methodName("getCurrentDateTime")
                .inputSchema("{\"type\":\"object\",\"properties\":{}}")
                .outputSchema("ISO-8601 timestamp string")
                .orderIndex(10)
                .build());
        defaults.add(RobotToolConfig.builder()
                .key("spring.math")
                .name("Math Toolkit")
                .description("执行加减乘除等常见数学运算")
                .category("utility")
                .icon("➗")
                .bindingType("CLASS")
                .className("com.bytedesk.ai.utils.tools.MathTools")
                .methodName(null)
                .inputSchema("See individual @Tool annotations on MathTools")
                .orderIndex(20)
                .build());
        defaults.add(RobotToolConfig.builder()
                .key("spring.weather")
                .name("Weather (Function Bean)")
                .description("查询指定城市的实时天气。依赖 WeatherTools#currentWeather 函数 bean")
                .category("data")
                .icon("☁️")
                .bindingType("SPRING_BEAN")
                .beanName("currentWeather")
                .inputSchema("{\"type\":\"object\",\"properties\":{\"location\":{\"type\":\"string\"}}}")
                .orderIndex(30)
                .build());
        defaults.add(RobotToolConfig.builder()
                .key("spring.datetime.alarm")
                .name("Alarm Planner")
                .description("允许访客设置未来提醒时间，演示 MethodToolCallback 用法")
                .category("workflow")
                .icon("⏰")
                .bindingType("CLASS")
                .className("com.bytedesk.ai.utils.tools.DateTimeTools")
                .methodName("setAlarm")
                .inputSchema("{\"type\":\"object\",\"properties\":{\"time\":{\"type\":\"string\",\"format\":\"date-time\"}}}")
                .orderIndex(40)
                .build());
        return defaults;
    }
}
