package com.bytedesk.ticket.ticket_settings_auto_create;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.thread.enums.ThreadCloseTypeEnum;

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

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ticket_auto_create_settings")
public class TicketAutoCreateSettingsEntity extends BaseEntity {

    private static final List<String> SUPPORTED_AUTO_CREATE_CLOSE_TYPES = List.of(
            ThreadCloseTypeEnum.AUTO.name(),
            ThreadCloseTypeEnum.AGENT.name(),
            ThreadCloseTypeEnum.VISITOR.name());

    private static final long serialVersionUID = 1L;

    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = Boolean.FALSE;

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "close_types", length = 512)
    private List<String> closeTypes = defaultCloseTypes();

    @Builder.Default
    private Integer minVisitorMessageCount = 2;

    @Builder.Default
    private Integer minRobotMessageCount = 1;

    @Builder.Default
    private Boolean requireAiUnresolved = Boolean.TRUE;

    @Builder.Default
    private Boolean requireAgentOffline = Boolean.FALSE;

    @Builder.Default
    private Boolean skipIfTicketExists = Boolean.TRUE;

    /**
     * 自动建单时用于智能生成工单内容的 RobotEntity.uid。
     * 为空时使用默认的工单生成智能体（ROBOT_NAME_TICKET_GENERATE）。
     */
    @Column(name = "auto_ticket_robot_uid", length = 64)
    private String autoTicketRobotUid;

    /**
     * 是否启用自动建单时间段限制：仅当当前时刻（按时区计算）落在时间段内时才允许自动建单。
     */
    @Builder.Default
    @Column(name = "time_window_enabled")
    private Boolean timeWindowEnabled = Boolean.FALSE;

    /**
     * 时间段开始时间（HH:mm），默认 09:00。
     */
    @Builder.Default
    @Column(name = "time_window_start_time", length = 8)
    private String timeWindowStartTime = "09:00";

    /**
     * 时间段结束时间（HH:mm），默认 18:00。早于开始时间视为跨天时段（如 22:00–06:00）。
     */
    @Builder.Default
    @Column(name = "time_window_end_time", length = 8)
    private String timeWindowEndTime = "18:00";

    /**
     * 判定当前时刻是否在时间段内所使用的时区，默认 Asia/Shanghai。
     */
    @Builder.Default
    @Column(name = "time_window_timezone", length = 64)
    private String timeWindowTimezone = "Asia/Shanghai";

    public static TicketAutoCreateSettingsEntity fromRequest(TicketAutoCreateSettingsRequest request) {
        TicketAutoCreateSettingsEntity entity = TicketAutoCreateSettingsEntity.builder().build();
        applyRequest(entity, request);
        return entity;
    }

    public static List<String> defaultCloseTypes() {
        return new ArrayList<>(List.of(ThreadCloseTypeEnum.AUTO.name()));
    }

    public static boolean isSupportedAutoCreateCloseType(String value) {
        return SUPPORTED_AUTO_CREATE_CLOSE_TYPES.contains(value);
    }

    public static List<String> normalizeCloseTypes(List<String> values) {
        if (values == null || values.isEmpty()) {
            return defaultCloseTypes();
        }
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String value : values) {
            if (!StringUtils.hasText(value)) {
                continue;
            }
            String closeType = value.trim().toUpperCase(Locale.ROOT);
            if (ThreadCloseTypeEnum.NONE.name().equals(closeType)) {
                continue;
            }
            if (isSupportedAutoCreateCloseType(closeType)) {
                normalized.add(closeType);
            }
        }
        return normalized.isEmpty() ? defaultCloseTypes() : new ArrayList<>(normalized);
    }

    public static void applyRequest(TicketAutoCreateSettingsEntity entity, TicketAutoCreateSettingsRequest request) {
        if (entity == null || request == null) {
            return;
        }
        if (request.getEnabled() != null) {
            entity.setEnabled(request.getEnabled());
        }
        if (request.getCloseTypes() != null) {
            entity.setCloseTypes(normalizeCloseTypes(request.getCloseTypes()));
        } else if (entity.getCloseTypes() == null || entity.getCloseTypes().isEmpty()) {
            entity.setCloseTypes(defaultCloseTypes());
        }
        if (request.getMinVisitorMessageCount() != null) {
            entity.setMinVisitorMessageCount(request.getMinVisitorMessageCount());
        }
        if (request.getMinRobotMessageCount() != null) {
            entity.setMinRobotMessageCount(request.getMinRobotMessageCount());
        }
        if (request.getRequireAiUnresolved() != null) {
            entity.setRequireAiUnresolved(request.getRequireAiUnresolved());
        }
        if (request.getRequireAgentOffline() != null) {
            entity.setRequireAgentOffline(request.getRequireAgentOffline());
        }
        if (request.getSkipIfTicketExists() != null) {
            entity.setSkipIfTicketExists(request.getSkipIfTicketExists());
        }
        if (request.getAutoTicketRobotUid() != null || !StringUtils.hasText(entity.getAutoTicketRobotUid())) {
            entity.setAutoTicketRobotUid(request.getAutoTicketRobotUid());
        }
        if (request.getTimeWindowEnabled() != null) {
            entity.setTimeWindowEnabled(request.getTimeWindowEnabled());
        }
        if (StringUtils.hasText(request.getTimeWindowStartTime())) {
            entity.setTimeWindowStartTime(request.getTimeWindowStartTime().trim());
        } else if (!StringUtils.hasText(entity.getTimeWindowStartTime())) {
            entity.setTimeWindowStartTime("09:00");
        }
        if (StringUtils.hasText(request.getTimeWindowEndTime())) {
            entity.setTimeWindowEndTime(request.getTimeWindowEndTime().trim());
        } else if (!StringUtils.hasText(entity.getTimeWindowEndTime())) {
            entity.setTimeWindowEndTime("18:00");
        }
        if (StringUtils.hasText(request.getTimeWindowTimezone())) {
            entity.setTimeWindowTimezone(request.getTimeWindowTimezone().trim());
        } else if (!StringUtils.hasText(entity.getTimeWindowTimezone())) {
            entity.setTimeWindowTimezone("Asia/Shanghai");
        }
    }
}