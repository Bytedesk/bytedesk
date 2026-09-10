package com.bytedesk.ticket.ticket_settings_auto_create;

import java.util.List;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TicketAutoCreateSettingsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;

    private List<String> closeTypes;

    private Integer minVisitorMessageCount;

    private Integer minRobotMessageCount;

    private Boolean requireAiUnresolved;

    private Boolean requireAgentOffline;

    private Boolean skipIfTicketExists;

    private String autoTicketRobotUid;

    /** 是否启用自动建单时间段限制 */
    private Boolean timeWindowEnabled;

    /** 时间段开始时间（HH:mm） */
    private String timeWindowStartTime;

    /** 时间段结束时间（HH:mm），早于开始时间视为跨天时段 */
    private String timeWindowEndTime;

    /** 判定时间段所用时区 */
    private String timeWindowTimezone;
}