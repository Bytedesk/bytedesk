/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:31:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-14 17:01:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.rbac.user.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Entity
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ ActionEntityListener.class })
@Table(name = "bytedesk_core_action")
public class ActionEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String title;

    private String action;

    private String description;

    private String ip;

    // according to ip address
    private String ipLocation;

    @Builder.Default
    // @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    // private String type = TypeConsts.ACTION_TYPE_LOG;
    // private ActionTypeEnum type = ActionTypeEnum.LOG;
    private String type = ActionTypeEnum.LOG.name();

    // action failed object
    @Builder.Default
    // json字段格式，搜索时，对数据库有依赖，不方便迁移
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // @JdbcTypeCode(SqlTypes.JSON)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    // private String userUid;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Builder.Default
     private String platform = PlatformEnum.BYTEDESK.name();

}
