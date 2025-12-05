/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-04
 * @Description: Robot-to-agent transition settings entity
 */
package com.bytedesk.service.robot_to_agent_settings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.utils.TransferKeywordUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Entity
@Table(
    name = "bytedesk_service_robot_to_agent_settings",
    indexes = {
        @Index(name = "idx_robot_to_agent_settings_uid", columnList = "uuid")
    }
)
public class RobotToAgentSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MANUAL_TRANSFER_LABEL = "转人工客服";
    private static final int DEFAULT_MIN_CONFIDENCE = 65;
    private static final int DEFAULT_MAX_ROBOT_REPLIES = 3;
    private static final int DEFAULT_AUTO_TRANSFER_DELAY_SECONDS = 0;
    private static final int DEFAULT_COOLDOWN_SECONDS = 90;

    @Builder.Default
    private Boolean enabled = Boolean.TRUE;

    @Builder.Default
    private Boolean keywordTriggerEnabled = Boolean.TRUE;

    @Builder.Default
    private Integer minConfidence = DEFAULT_MIN_CONFIDENCE;

    @Builder.Default
    private Integer maxRobotRepliesBeforeTransfer = DEFAULT_MAX_ROBOT_REPLIES;

    @Builder.Default
    private Integer autoTransferDelaySeconds = DEFAULT_AUTO_TRANSFER_DELAY_SECONDS;

    @Builder.Default
    private Integer cooldownSeconds = DEFAULT_COOLDOWN_SECONDS;

    @Builder.Default
    private Boolean allowVisitorManualTransfer = Boolean.TRUE;

    @Builder.Default
    private String manualTransferLabel = DEFAULT_MANUAL_TRANSFER_LABEL;

    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    @Convert(converter = StringListConverter.class)
    private List<String> triggerKeywords = new ArrayList<>(TransferKeywordUtil.getDefaultKeywords());

    public static RobotToAgentSettingsEntity fromRequest(RobotToAgentSettingsRequest request, ModelMapper modelMapper) {
        RobotToAgentSettingsEntity entity;
        if (request == null) {
            entity = RobotToAgentSettingsEntity.builder().build();
        } else if (modelMapper != null) {
            entity = modelMapper.map(request, RobotToAgentSettingsEntity.class);
        } else {
            entity = RobotToAgentSettingsEntity.builder()
                    .enabled(request.getEnabled())
                    .keywordTriggerEnabled(request.getKeywordTriggerEnabled())
                    .minConfidence(request.getMinConfidence())
                    .maxRobotRepliesBeforeTransfer(request.getMaxRobotRepliesBeforeTransfer())
                    .autoTransferDelaySeconds(request.getAutoTransferDelaySeconds())
                    .cooldownSeconds(request.getCooldownSeconds())
                    .allowVisitorManualTransfer(request.getAllowVisitorManualTransfer())
                    .manualTransferLabel(request.getManualTransferLabel())
                    .triggerKeywords(request.getTriggerKeywords() != null
                            ? new ArrayList<>(request.getTriggerKeywords())
                            : null)
                    .build();
        }
        ensureDefaults(entity);
        return entity;
    }

    public static void ensureDefaults(RobotToAgentSettingsEntity entity) {
        if (entity == null) {
            return;
        }
        if (entity.getEnabled() == null) {
            entity.setEnabled(Boolean.TRUE);
        }
        if (entity.getKeywordTriggerEnabled() == null) {
            entity.setKeywordTriggerEnabled(Boolean.TRUE);
        }
        if (entity.getMinConfidence() == null) {
            entity.setMinConfidence(DEFAULT_MIN_CONFIDENCE);
        }
        if (entity.getMaxRobotRepliesBeforeTransfer() == null) {
            entity.setMaxRobotRepliesBeforeTransfer(DEFAULT_MAX_ROBOT_REPLIES);
        }
        if (entity.getAutoTransferDelaySeconds() == null) {
            entity.setAutoTransferDelaySeconds(DEFAULT_AUTO_TRANSFER_DELAY_SECONDS);
        }
        if (entity.getCooldownSeconds() == null) {
            entity.setCooldownSeconds(DEFAULT_COOLDOWN_SECONDS);
        }
        if (entity.getAllowVisitorManualTransfer() == null) {
            entity.setAllowVisitorManualTransfer(Boolean.TRUE);
        }
        if (!StringUtils.hasText(entity.getManualTransferLabel())) {
            entity.setManualTransferLabel(DEFAULT_MANUAL_TRANSFER_LABEL);
        }
        normalizeKeywords(entity);
    }

    public static void normalizeKeywords(RobotToAgentSettingsEntity entity) {
        if (entity == null) {
            return;
        }
        List<String> keywords = entity.getTriggerKeywords();
        if (keywords == null || keywords.isEmpty()) {
            entity.setTriggerKeywords(new ArrayList<>(TransferKeywordUtil.getDefaultKeywords()));
            return;
        }
        List<String> sanitized = keywords.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        if (sanitized.isEmpty()) {
            entity.setTriggerKeywords(new ArrayList<>(TransferKeywordUtil.getDefaultKeywords()));
        } else {
            entity.setTriggerKeywords(new ArrayList<>(sanitized));
        }
    }
}
