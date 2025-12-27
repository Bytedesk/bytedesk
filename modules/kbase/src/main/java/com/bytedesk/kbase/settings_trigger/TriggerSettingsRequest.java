/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-23
 * @Description: Trigger settings request DTO
 */
package com.bytedesk.kbase.settings_trigger;

import java.io.Serializable;
import java.util.List;

import com.bytedesk.kbase.trigger.TriggerRequestSimple;

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
public class TriggerSettingsRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Trigger entity list to be associated with this settings.
     * - null: do not change existing associations
     * - []  : clear existing associations
     */
    private List<TriggerRequestSimple> triggers;

    // @Builder.Default
    // private Integer maxProactiveCount = 3;

    // @Builder.Default
    // private Integer proactiveInterval = 600;

    // @Builder.Default
    // private String triggerConditions = BytedeskConsts.EMPTY_JSON_STRING;

    // @Builder.Default
    // private List<String> proactiveFaqUids = new ArrayList<>();
}
