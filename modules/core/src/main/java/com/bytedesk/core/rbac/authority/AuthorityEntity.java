/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-14 15:56:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.authority;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import com.bytedesk.core.base.BaseEntityNoOrg;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;

import jakarta.persistence.*;

/**
 * @author im.bytedesk.com
 */
@Getter
@Setter
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "bytedesk_core_authority")
public class AuthorityEntity extends BaseEntityNoOrg {

    private static final long serialVersionUID = 1L;

    private String name;

    @Column(name = "authority_value")
    private String value;
    
    private String description;

    // platform: 只有超级管理员才有权限
    // organization: 管理员才有权限
    @Builder.Default
    private String level = LevelEnum.ORGANIZATION.name();

    // 默认bytedesk平台
    @Builder.Default
    private String platform = PlatformEnum.BYTEDESK.name();

}
