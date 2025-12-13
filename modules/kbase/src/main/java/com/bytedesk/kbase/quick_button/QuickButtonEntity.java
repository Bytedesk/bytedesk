/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-11
 * @Description: Persistent quick button configuration for chat toolbar
 */
package com.bytedesk.kbase.quick_button;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "bytedesk_kbase_quick_button",
    indexes = {
        @Index(name = "idx_quick_button_uid", columnList = "uuid")
    }
)
public class QuickButtonEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @Column(length = 128)
    private String title;

    @Column(length = 256)
    private String subtitle;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String description;

    @Column(length = 64)
    private String icon;

    @Column(length = 32)
    private String color;

    @Column(length = 64)
    private String badge;

    @Column(length = 64)
    private String code;

    @Column(name = "button_image", length = 512)
    private String imageUrl;

    @Column(name = "button_type", length = 32)
    @Builder.Default
    private String type = QuickButtonTypeEnum.FAQ.name();

    @Builder.Default
    @Column(name = "button_order")
    private Integer orderIndex = 0;

    @Builder.Default
    @Column(name = "is_highlight")
    private Boolean highlight = false;

    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    @Column(name = "kb_uid", length = 64)
    private String kbUid;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    @Column(name = "button_payload", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    @Convert(converter = QuickButtonPayloadConverter.class)
    private QuickButtonPayload payload;
}
