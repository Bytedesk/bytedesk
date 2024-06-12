/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:31:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-07 14:44:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.rbac.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 
 */
@Data
@Entity
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "core_action")
public class Action extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String title;

    private String action;

    private String description;

    private String ip;

    // according to ip address
    private String ipLocation;

    @Builder.Default
    @Column(name = TypeConsts.COLUMN_NAME_TYPE)
    private String type = TypeConsts.ACTION_TYPE_LOG;

    // action failed object
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String extra = BdConstants.EMPTY_JSON_STRING;

    // private String userUid;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String orgUid;

    @Builder.Default
    private String platform = BdConstants.PLATFORM_BYTEDESK;
}
