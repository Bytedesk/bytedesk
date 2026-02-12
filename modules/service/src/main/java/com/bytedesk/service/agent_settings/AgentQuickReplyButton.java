/*
 * @Author: jackning 270580156@qq.com
 * @Description: Agent quick reply button visibility config
 */
package com.bytedesk.service.agent_settings;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentQuickReplyButton implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Quick reply button code, e.g. emoji/upload/RATE_INVITE */
    private String code;

    /** Whether the button is enabled */
    private Boolean enabled;
}
