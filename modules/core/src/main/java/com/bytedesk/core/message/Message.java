/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-07 14:48:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.thread.Thread;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "core_message")
public class Message extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    // 尽管注释掉此行，在数据库中使用int类型存储，但前端查询的时候还是显示为字符串类型
    // @Enumerated(EnumType.STRING) // 默认使用int类型表示，如果为了可读性，可以转换为使用字符串存储
    @Column(name = TypeConsts.COLUMN_NAME_TYPE)
    // private String type;
    private MessageTypeEnum type = MessageTypeEnum.TEXT;

    // 复杂类型可以使用json存储在此，通过type字段区分
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    // @Builder.Default
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // @JdbcTypeCode(SqlTypes.JSON)
    // private String extra = BdConstants.EMPTY_JSON_STRING;

    @Builder.Default
    private MessageStatusEnum status = MessageStatusEnum.SENT;

    // private String client;
    private ClientEnum client;

    /** message belongs to */
    @JsonIgnore
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Thread> threads = new ArrayList<>();

    /**
     * sender
     * 考虑到访客信息不存储在user表中，在visitor表中，此处使用json存储，加快查询速度，
     * 以空间换时间
     */
    // @ManyToOne(fetch = FetchType.EAGER)
    // private User user;
    //
    // h2 db 不能使用 user, 所以重定义为 by_user
    @Builder.Default
    @Column(name = "by_user", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String user = BdConstants.EMPTY_JSON_STRING;

    /** belong to org */
    private String orgUid;

}
