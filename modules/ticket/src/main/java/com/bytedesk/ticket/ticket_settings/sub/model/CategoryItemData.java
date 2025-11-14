package com.bytedesk.ticket.ticket_settings.sub.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单个工单分类项的结构化数据。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryItemData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类唯一标识（对访客暴露）。 */
    private String uid;

    /** 分类显示名称。 */
    private String name;

    /** 分类描述或提示。 */
    private String description;

    /** 是否启用。 */
    @Builder.Default
    private Boolean enabled = Boolean.TRUE;

    /** 是否为默认分类。 */
    @Builder.Default
    private Boolean defaultCategory = Boolean.FALSE;

    /** 排序序号，越小越靠前。 */
    @Builder.Default
    private Integer orderIndex = 0;
}