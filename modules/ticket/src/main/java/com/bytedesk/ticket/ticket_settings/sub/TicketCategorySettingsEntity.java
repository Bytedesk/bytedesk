package com.bytedesk.ticket.ticket_settings.sub;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.ticket.ticket_settings.sub.converter.CategorySettingsConverter;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketCategorySettingsRequest;
import com.bytedesk.ticket.ticket_settings.sub.model.CategoryItemData;
import com.bytedesk.ticket.ticket_settings.sub.model.CategorySettingsData;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketCategoryItemRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 工单分类设置实体，采用 JSON 列存储所有分类项。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ticket_category_settings")
public class TicketCategorySettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    @Convert(converter = CategorySettingsConverter.class)
    @Column(length = 4096)
    private CategorySettingsData content = CategorySettingsData.builder().build();

    public static TicketCategorySettingsEntity fromRequest(TicketCategorySettingsRequest request, Supplier<String> uidSupplier) {
        TicketCategorySettingsEntity entity = new TicketCategorySettingsEntity();
        entity.setContent(buildContent(request, uidSupplier));
        return entity;
    }

    public void replaceFromRequest(TicketCategorySettingsRequest request, Supplier<String> uidSupplier) {
        this.content = buildContent(request, uidSupplier);
    }

    private static CategorySettingsData buildContent(
            TicketCategorySettingsRequest request,
            Supplier<String> uidSupplier) {
        CategorySettingsData data = CategorySettingsData.builder().build();
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            data.normalize();
            return data;
        }
        List<CategoryItemData> items = new ArrayList<>();
        int order = 0;
        for (TicketCategoryItemRequest itemReq : request.getItems()) {
            if (itemReq == null) {
                continue;
            }
            CategoryItemData item = CategoryItemData.builder()
                    .uid(itemReq.getUid() != null ? itemReq.getUid() : uidSupplier.get())
                    .name(itemReq.getName())
                    .description(itemReq.getDescription())
                    .enabled(itemReq.getEnabled())
                    .defaultCategory(itemReq.getDefaultCategory())
                    .orderIndex(itemReq.getOrderIndex() != null ? itemReq.getOrderIndex() : order)
                    .build();
            items.add(item);
            order++;
        }
        data.setItems(items);
        data.normalize();
        return data;
    }
}