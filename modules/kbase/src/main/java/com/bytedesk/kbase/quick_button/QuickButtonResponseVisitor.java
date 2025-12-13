package com.bytedesk.kbase.quick_button;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight quick button DTO for visitor/robot payloads.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuickButtonResponseVisitor implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;

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

    private QuickButtonPayload payload;

    public static QuickButtonResponseVisitor fromEntity(QuickButtonEntity entity) {
        if (entity == null) {
            return null;
        }
        return QuickButtonResponseVisitor.builder()
                .uid(entity.getUid())
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
                .payload(entity.getPayload())
                .build();
    }

    public static List<QuickButtonResponseVisitor> fromEntities(List<QuickButtonEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(QuickButtonResponseVisitor::fromEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}