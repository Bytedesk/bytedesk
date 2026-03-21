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

    private Boolean enabled;

    private String number;

    private String displayName;

    private String target;
}