/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-03-17
 * @Description: Shared call center settings entity for reusable settings templates
 */
package com.bytedesk.call.call_settings;

import org.modelmapper.ModelMapper;

import com.bytedesk.core.base.BaseEntity;

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
        @Index(name = "idx_call_settings_uid", columnList = "uuid")
    }
)
public class CallSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Whether the call settings are enabled.
     */
    @Builder.Default
    private Boolean enabled = false;

    /**
     * External phone number shown to users for inbound or outbound calls.
     * Example: hotline number, DID number, or displayed caller number.
     */
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

    public static CallSettingsEntity fromRequest(CallSettingsRequest request, ModelMapper modelMapper) {
        if (request == null || modelMapper == null) {
            return CallSettingsEntity.builder().build();
        }
        return modelMapper.map(request, CallSettingsEntity.class);
    }
}