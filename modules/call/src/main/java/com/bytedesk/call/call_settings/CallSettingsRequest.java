/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-03-17
 * @Description: Request DTO for shared call center settings
 */
package com.bytedesk.call.call_settings;

import com.bytedesk.core.base.BaseRequest;

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
public class CallSettingsRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * String-based reference to AgentEntity.uid.
     */
    private String agentUid;

    /**
     * Whether the call settings are enabled.
     */
    private Boolean enabled;

    /**
     * External phone number shown to users for inbound or outbound calls.
     */
    private String number;

    /**
     * Display name shown in call related UI.
     */
    private String displayName;

    /**
     * Internal extension or SIP target used by CTI/SIP registration and dialing.
     */
    private String target;

    /**
     * Hold media URL played to the customer side while the call is on hold.
     */
    private String holdMediaUrl;

    /**
     * Internal extension numbers available for consultation, separated by comma/semicolon/space.
     */
    private String consultExtensionNumbers;

    /**
     * Target numbers available for transfer, separated by comma/semicolon/space.
     */
    private String transferTargetNumbers;

    /**
     * Target numbers available for conference invite, separated by comma/semicolon/space.
     */
    private String conferenceTargetNumbers;

    /**
     * IVR extension numbers available for transfer, separated by comma/semicolon/space.
     */
    private String ivrTargetNumbers;
}