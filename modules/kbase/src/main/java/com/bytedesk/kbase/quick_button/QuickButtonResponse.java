/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-11
 * @Description: Response DTO for quick buttons
 */
package com.bytedesk.kbase.quick_button;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Response DTO for quick buttons used in admin surfaces.
 */

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class QuickButtonResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String title;

    private String subtitle;

    private String description;

    private String icon;

    private String color;

    private String badge;

    private String code;

    private String imageUrl;

    private String type;

    private Integer orderIndex;

    private Boolean highlight;

    private Boolean enabled;

    private String kbUid;

    private String content;

    // private QuickButtonPayload payload;

    public static QuickButtonResponse fromEntity(QuickButtonEntity entity) {
        if (entity == null) {
            return null;
        }
        return QuickButtonResponse.builder()
                .uid(entity.getUid())
                .userUid(entity.getUserUid())
                .orgUid(entity.getOrgUid())
                .level(entity.getLevel())
                .platform(entity.getPlatform())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .title(entity.getTitle())
                .subtitle(entity.getSubtitle())
                .description(entity.getDescription())
                .icon(entity.getIcon())
                .color(entity.getColor())
                .badge(entity.getBadge())
                .code(entity.getCode())
                .imageUrl(entity.getImageUrl())
                .type(entity.getType())
                .orderIndex(entity.getOrderIndex())
                .highlight(entity.getHighlight())
                .enabled(entity.getEnabled())
                .kbUid(entity.getKbUid())
                .content(entity.getContent())
                // .payload(entity.getPayload())
                .build();
    }

    public static List<QuickButtonResponse> fromEntities(List<QuickButtonEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(QuickButtonResponse::fromEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
