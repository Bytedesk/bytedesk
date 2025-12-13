/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-11
 * @Description: Request payload for quick button CRUD
 */
package com.bytedesk.kbase.quick_button;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class QuickButtonRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String title;

    private String subtitle;

    private String description;

    private String icon;

    private String color;

    private String badge;

    private String code;

    private String imageUrl;

    private Integer orderIndex;

    private Boolean highlight;

    private Boolean enabled;

    private String kbUid;

    /** Structured payload for action type specific data */
    private QuickButtonPayload payload;
}
