/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-23
 * @Description: Trigger settings response DTO
 */
package com.bytedesk.kbase.settings_trigger;

import java.io.Serializable;
import java.util.List;

import com.bytedesk.kbase.trigger.TriggerResponseSimple;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TriggerSettingsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<TriggerResponseSimple> triggers;

    public static TriggerSettingsResponse fromEntity(TriggerSettingsEntity settings) {
        if (settings == null) {
            return null;
        }
        return TriggerSettingsResponse.builder()
                .triggers(settings.getTriggers() == null ? null
                        : settings.getTriggers().stream().map(t -> TriggerResponseSimple.builder()
                                .uid(t.getUid())
                                .triggerKey(t.getTriggerKey())
                                .enabled(t.getEnabled())
                                .type(t.getType())
                                .name(t.getName())
                                .description(t.getDescription())
                                .config(t.getConfig())
                                .build()).toList())
                .build();
    }
}
