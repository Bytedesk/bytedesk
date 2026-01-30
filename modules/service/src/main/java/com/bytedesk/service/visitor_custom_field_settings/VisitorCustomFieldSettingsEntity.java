/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-27
 * @Description: Visitor custom field settings per organization
 */
package com.bytedesk.service.visitor_custom_field_settings;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.CustomFieldItemListConverter;
import com.bytedesk.core.model.CustomFieldItem;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Store visitor custom field definitions at org level.
 *
 * Note:
 * - The definitions (nickname/key) are configured per org.
 * - The values are stored on each VisitorEntity.customFieldList.
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "bytedesk_service_visitor_custom_field_settings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_visitor_custom_field_settings_org_uid", columnNames = { "org_uid" })
        },
        indexes = {
                @Index(name = "idx_visitor_custom_field_settings_org_uid", columnList = "org_uid")
        })
public class VisitorCustomFieldSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Custom field definitions for this org.
     * Each item uses: nickname (display name) + key (unique identifier).
     */
    @lombok.Builder.Default
    @Convert(converter = CustomFieldItemListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<CustomFieldItem> customFieldList = new ArrayList<>();
}
