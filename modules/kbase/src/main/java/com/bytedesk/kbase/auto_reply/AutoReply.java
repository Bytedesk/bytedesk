/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 15:50:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-01 10:41:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply;

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
 * 固定自动回复，无需匹配规则，直接回复。
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "kb_auto_reply")
public class AutoReply extends BaseEntity {

    @Builder.Default
    // 如果使用int存储，enum中类型的顺序改变，会导致数据库中的数据类型改变，导致无法查询到数据
    // @Enumerated(EnumType.STRING) // 默认使用int类型表示，如果为了可读性，可以转换为使用字符串存储
    @Column(name = "message_type", nullable = false)
    // private MessageTypeEnum type = MessageTypeEnum.TEXT;
    private String type = MessageTypeEnum.TEXT.name();

    //
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    private String categoryUid; // 文章分类。生成页面时，先查询分类，后通过分类查询相关文章。

    private String kbUid; // 对应知识库
    
    // user uid
    private String userUid;
}