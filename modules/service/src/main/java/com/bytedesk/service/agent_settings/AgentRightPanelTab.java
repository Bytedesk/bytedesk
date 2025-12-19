/*
 * @Author: jackning 270580156@qq.com
 * @Description: Agent right panel dynamic tab config (title + iframe url)
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
public class AgentRightPanelTab implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Tab title shown in desktop RightPanel */
    private String title;

    /** Third-party business system URL to be embedded in iframe */
    private String url;
}
