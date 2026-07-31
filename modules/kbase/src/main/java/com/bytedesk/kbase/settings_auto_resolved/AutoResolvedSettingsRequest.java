/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-28
 * @Description: Auto resolved timeout settings request
 */
package com.bytedesk.kbase.settings_auto_resolved;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class AutoResolvedSettingsRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private Boolean enabled = false;

    @Builder.Default
    private Integer timeoutHours = 24;

    @Builder.Default
    private Boolean notifyVisitorOnAutoResolved = false;
}