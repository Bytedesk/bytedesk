/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-03-17
 * @Description: Shared call center settings entity for reusable settings templates
 */
package com.bytedesk.call.call_settings;

import org.modelmapper.ModelMapper;

import com.bytedesk.call.config.CallConstants;
import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Entity
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(
    name = "bytedesk_call_settings",
    indexes = {
        @Index(name = "idx_call_settings_uid", columnList = "uuid"),
        @Index(name = "idx_call_settings_agent_uid", columnList = "agent_uid"),
        @Index(name = "idx_call_settings_org_uid", columnList = "org_uid")
    }
)
public class CallSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * String-based reference to AgentEntity.uid.
     * Kept as a plain UID to avoid call -> service module entity dependency.
     */
    @Column(name = "agent_uid", length = 64)
    private String agentUid;

    /**
     * Whether the call settings are enabled.
     */
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = false;

    /**
     * External phone number shown to users for inbound or outbound calls.
     * Example: hotline number, DID number, or displayed caller number.
     */
    @Column(name = "phone_number")
    private String number;

    /**
     * Display name shown in call related UI.
     */
    private String displayName;

    /**
     * Internal extension or SIP target used by CTI/SIP registration and dialing.
     * Example: 1000, sip:1000@pbx.local.
     */
    private String target;

    /**
     * Hold media URL played to the customer side while the agent puts the call on hold.
     */
    @Builder.Default
    @Column(name = "hold_media_url", length = 1024)
    private String holdMediaUrl = CallConstants.DEFAULT_HOLD_MEDIA_URL;

    @Builder.Default
    @Column(name = "consult_extension_numbers", length = 512)
    private String consultExtensionNumbers = CallConstants.DEFAULT_CONSULT_EXTENSION_NUMBERS;

    @Builder.Default
    @Column(name = "transfer_target_numbers", length = 512)
    private String transferTargetNumbers = CallConstants.DEFAULT_TRANSFER_TARGET_NUMBERS;

    @Builder.Default
    @Column(name = "conference_target_numbers", length = 512)
    private String conferenceTargetNumbers = CallConstants.DEFAULT_CONFERENCE_TARGET_NUMBERS;

    @Builder.Default
    @Column(name = "ivr_target_numbers", length = 512)
    private String ivrTargetNumbers = CallConstants.DEFAULT_IVR_TARGET_NUMBERS;

    public static CallSettingsEntity fromRequest(CallSettingsRequest request, ModelMapper modelMapper) {
        if (request == null || modelMapper == null) {
            return CallSettingsEntity.builder().build();
        }
        return modelMapper.map(request, CallSettingsEntity.class);
    }
}