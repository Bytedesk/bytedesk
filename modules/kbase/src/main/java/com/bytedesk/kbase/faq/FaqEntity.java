/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:16:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-23 16:59:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.message.MessageTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * faq: Frequently Asked Questions
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_kb_faq")
public class FaqEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String title;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    @Builder.Default
    // @Enumerated(EnumType.STRING)
    @Column(name = "faq_type", nullable = false)
    // private MessageTypeEnum type = MessageTypeEnum.TEXT;
    private String type = MessageTypeEnum.TEXT.name();

    // 被点击查看次数
    @Builder.Default
    private int viewCount = 0;

    // 点赞次数
    @Builder.Default
    private int upCount = 0;

    // 点踩次数
    @Builder.Default
    private int downCount = 0;

    // @JsonIgnore
    // @ManyToOne
    // private Category category;

    private String categoryUid; // 分类

    private String kbUid; // 对应知识库

    public void up() {
        this.setUpCount(this.upCount + 1);
    }

    public void down() {
        this.setDownCount(this.downCount + 1);
    }

}
