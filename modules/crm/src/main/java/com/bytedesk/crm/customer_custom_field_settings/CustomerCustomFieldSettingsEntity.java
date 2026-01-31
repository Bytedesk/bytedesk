/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-27
 * @Description: Customer custom field settings per organization
 */
package com.bytedesk.crm.customer_custom_field_settings;

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
 * Store customer custom field definitions at org level.
 *
 * Note:
 * - The definitions (nickname/key) are configured per org.
 * - The values are stored on each CustomerEntity.customFieldList.
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "bytedesk_crm_customer_custom_field_settings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_customer_custom_field_settings_org_uid", columnNames = { "org_uid" })
        },
        indexes = {
                @Index(name = "idx_customer_custom_field_settings_org_uid", columnList = "org_uid")
        })
public class CustomerCustomFieldSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @lombok.Builder.Default
    @Convert(converter = CustomFieldItemListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<CustomFieldItem> customFieldList = new ArrayList<>();
}
