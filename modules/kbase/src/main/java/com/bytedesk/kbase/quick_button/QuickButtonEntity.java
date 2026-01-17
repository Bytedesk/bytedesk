/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-11
 * @Description: Persistent quick button configuration for chat toolbar
 */
package com.bytedesk.kbase.quick_button;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.base.BaseContent;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.EmailAddressContent;
import com.bytedesk.core.message.content.FaqContent;
import com.bytedesk.core.message.content.FormContent;
import com.bytedesk.core.message.content.GoodsContent;
import com.bytedesk.core.message.content.ImageContent;
import com.bytedesk.core.message.content.OrderContent;
import com.bytedesk.core.message.content.PhoneNumberContent;
import com.bytedesk.core.message.content.UrlContent;
import com.bytedesk.core.message.content.WechatNumberContent;

import jakarta.persistence.Column;
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
    private String type = MessageTypeEnum.FAQ.name();

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

    /**
     * Parse the stored `content` string into a typed object according to `type`.
     *
     * - For TEXT: returns raw String
     * - For structured types: returns corresponding BaseContent subclass
     */
    public Object getContentObject() {
        MessageTypeEnum messageType = MessageTypeEnum.fromValue(this.type);
        switch (messageType) {
            case TEXT:
                return this.content;
            case FAQ:
                return BaseContent.fromJson(this.content, FaqContent.class);
            case URL:
                return BaseContent.fromJson(this.content, UrlContent.class);
            case FORM:
                return BaseContent.fromJson(this.content, FormContent.class);
            case IMAGE:
                return BaseContent.fromJson(this.content, ImageContent.class);
            case GOODS:
                return BaseContent.fromJson(this.content, GoodsContent.class);
            case ORDER:
                return BaseContent.fromJson(this.content, OrderContent.class);
            case PHONE_NUMBER:
                return BaseContent.fromJson(this.content, PhoneNumberContent.class);
            case EMAILL_ADDRESS:
                return BaseContent.fromJson(this.content, EmailAddressContent.class);
            case WECHAT_NUMBER:
                return BaseContent.fromJson(this.content, WechatNumberContent.class);
            default:
                return this.content;
        }
    }

    public <T extends BaseContent> T getContentAs(Class<T> clazz) {
        return BaseContent.fromJson(this.content, clazz);
    }

    public void setContentObject(Object contentObject) {
        if (contentObject == null) {
            this.content = null;
            return;
        }
        if (contentObject instanceof BaseContent bc) {
            this.content = bc.toJson();
            return;
        }
        if (contentObject instanceof String s) {
            this.content = s;
            return;
        }
        this.content = com.alibaba.fastjson2.JSON.toJSONString(contentObject);
    }
}
