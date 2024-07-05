/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:12:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-26 11:34:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.quick_reply;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.category.Category;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 常用语-快捷回复
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "service_quick_reply")
public class QuickReply extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String title;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "reply_type", nullable = false)
    // private String type;
    private MessageTypeEnum type = MessageTypeEnum.TEXT;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private LevelEnum level = LevelEnum.ORGNIZATION;

    @JsonIgnore
    @ManyToOne
    private Category category;
    
    // belongs to org
    // private String orgUid;

    // only used when created by user, not by org
    @JsonIgnore
    @ManyToOne
    private User user;

}
