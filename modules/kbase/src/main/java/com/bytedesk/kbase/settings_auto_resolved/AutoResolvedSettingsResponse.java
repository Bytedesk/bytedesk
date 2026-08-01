/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-28
 * @Description: Auto resolved timeout settings response
 */
package com.bytedesk.kbase.settings_auto_resolved;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AutoResolvedSettingsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;

    private Integer timeoutHours;

    private Boolean notifyVisitorOnAutoResolved;

    public static AutoResolvedSettingsResponse fromEntity(AutoResolvedSettingsEntity entity) {
        if (entity == null) {
            return null;
        }
        AutoResolvedSettingsResponse response = AutoResolvedSettingsResponse.builder()
                .enabled(entity.getEnabled())
                .timeoutHours(entity.getTimeoutHours())
                .notifyVisitorOnAutoResolved(entity.getNotifyVisitorOnAutoResolved())
                .build();
        response.setUid(entity.getUid());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}