package com.bytedesk.ticket.ticket_settings_visibility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ticket_visibility_settings")
public class TicketVisibilitySettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    @Convert(converter = TicketVisibilitySettingsConverter.class)
    @Column(length = 4096)
    private TicketVisibilitySettingsData content = TicketVisibilitySettingsData.builder().build();

    public static TicketVisibilitySettingsEntity fromRequest(TicketVisibilitySettingsRequest request) {
        TicketVisibilitySettingsEntity entity = TicketVisibilitySettingsEntity.builder().build();
        applyRequest(entity, request);
        return entity;
    }

    public static void applyRequest(TicketVisibilitySettingsEntity entity, TicketVisibilitySettingsRequest request) {
        if (entity == null || request == null) {
            return;
        }
        List<TicketVisibilityCategoryRuleData> rules = request.getCategoryRules() == null
                ? new ArrayList<>()
                : request.getCategoryRules().stream()
                        .map(rule -> TicketVisibilityCategoryRuleData.builder()
                                .categoryUid(rule.getCategoryUid())
                                .visibility(rule.getVisibility())
                                .build())
                        .collect(Collectors.toList());
        TicketVisibilitySettingsData data = TicketVisibilitySettingsData.builder()
                .mode(request.getMode())
                .categoryRules(rules)
                .build();
        data.normalize();
        entity.setContent(data);
    }
}