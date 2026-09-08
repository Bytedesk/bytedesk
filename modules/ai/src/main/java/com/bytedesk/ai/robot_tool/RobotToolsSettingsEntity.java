package com.bytedesk.ai.robot_tool;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Stores Spring AI tool orchestration preferences (draft + published) for a robot
 * settings template.
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bytedesk_ai_robot_tools_settings")
public class RobotToolsSettingsEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Builder.Default
    @Column(name = "is_tools_enabled")
    private Boolean enabled = false;

    @Builder.Default
    @Column(name = "invocation_mode")
    private String invocationMode = ToolInvocationMode.AUTO.name();

    @Builder.Default
    @Column(name = "tool_choice")
    private String toolChoice = ToolChoice.AUTO.name();

    @Builder.Default
    @Column(name = "max_tool_invocations")
    private Integer maxToolInvocations = 3;

    @Builder.Default
    @Column(name = "intent_recognition_enabled")
    private Boolean intentRecognitionEnabled = false;

    @Column(name = "intent_model")
    private String intentModel;

    @Column(name = "intent_provider")
    private String intentProviderUid;

    @Column(name = "tool_model")
    private String toolModel;

    @Column(name = "tool_provider")
    private String toolProviderUid;

    @Builder.Default
    @Column(name = "intent_timeout_ms")
    private Integer intentTimeoutMs = 1500;

    /**
     * 工具绑定列表默认为空：大模型对话中的 Tools/MCP 调用默认全部关闭，
     * 仅在管理后台（TabTools）手动添加并显式开启、发布后才会生效。
     */
    @Builder.Default
    @Convert(converter = RobotToolConfigListConverter.class)
    @Column(name = "tool_configs", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<RobotToolConfig> toolConfigs = new ArrayList<>();

    public static RobotToolsSettingsEntity fromRequest(RobotToolsSettingsRequest request, ModelMapper modelMapper) {
        if (modelMapper == null) {
            return RobotToolsSettingsEntity.builder().build();
        }
        if (request == null) {
            return RobotToolsSettingsEntity.builder().build();
        }
        RobotToolsSettingsEntity entity = modelMapper.map(request, RobotToolsSettingsEntity.class);
        if (entity.getToolConfigs() == null) {
            entity.setToolConfigs(new ArrayList<>());
        }
        return entity;
    }
}
