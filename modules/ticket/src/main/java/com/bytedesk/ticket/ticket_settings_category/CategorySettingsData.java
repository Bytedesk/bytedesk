package com.bytedesk.ticket.ticket_settings_category;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工单分类设置（草稿/发布）序列化数据。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySettingsData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private List<CategoryItemData> items = new ArrayList<>();

    public void normalize() {
        if (items == null) {
            items = new ArrayList<>();
            return;
        }
        // 填充默认值
        int index = 0;
        for (CategoryItemData item : items) {
            if (item.getOrderIndex() == null) {
                item.setOrderIndex(index);
            }
            if (item.getEnabled() == null) {
                item.setEnabled(Boolean.TRUE);
            }
            if (item.getDefaultCategory() == null) {
                item.setDefaultCategory(Boolean.FALSE);
            }
            index++;
        }
        items.sort(Comparator.comparing(CategoryItemData::getOrderIndex, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(CategoryItemData::getName, Comparator.nullsLast(String::compareToIgnoreCase)));

        // 确保最多一个默认分类；若没有但存在启用项，则选第一个启用项
        boolean hasDefault = false;
        for (CategoryItemData item : items) {
            if (Boolean.TRUE.equals(item.getDefaultCategory()) && Boolean.TRUE.equals(item.getEnabled())) {
                if (!hasDefault) {
                    hasDefault = true;
                } else {
                    item.setDefaultCategory(Boolean.FALSE);
                }
            }
        }
        if (!hasDefault) {
            items.stream()
                    .filter(i -> Boolean.TRUE.equals(i.getEnabled()))
                    .findFirst()
                    .ifPresent(i -> i.setDefaultCategory(true));
        }

        // 重写顺序为连续值
        int order = 0;
        for (CategoryItemData item : items) {
            item.setOrderIndex(order++);
        }
    }

    public long countEnabled() {
        return items == null ? 0 : items.stream().filter(i -> Boolean.TRUE.equals(i.getEnabled())).count();
    }

    public long countDisabled() {
        return items == null ? 0 : items.stream().filter(i -> !Boolean.TRUE.equals(i.getEnabled())).count();
    }

    public String resolveDefaultUid() {
        if (items == null) {
            return null;
        }
        return items.stream()
                .filter(i -> Boolean.TRUE.equals(i.getDefaultCategory()) && Boolean.TRUE.equals(i.getEnabled()))
                .map(CategoryItemData::getUid)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}