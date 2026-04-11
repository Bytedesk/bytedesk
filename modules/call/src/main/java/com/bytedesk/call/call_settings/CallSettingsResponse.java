/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-03-17
 * @Description: Response DTO for shared call center settings
 */
package com.bytedesk.call.call_settings;

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
public class CallSettingsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

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
}