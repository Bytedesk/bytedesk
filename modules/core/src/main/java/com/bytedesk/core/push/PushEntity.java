/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:30:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-21 17:14:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.enums.ClientEnum;

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
 * 
 */
@Data
@Entity
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_push")
public class PushEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String sender;

    private String content;

    private String receiver;

    @Column(name = "push_type")
    private String type;

    private String ip;

    // according to ip address
    private String ipLocation;

    private String deviceUid; // 设备唯一标识

    // @Enumerated(EnumType.STRING)
    @Builder.Default
    // private String status = StatusConsts.CODE_STATUS_PENDING;
    // private PushStatus status = PushStatus.PENDING;
    @Column(name = "push_status")
    private String status = PushStatusEnum.PENDING.name();

    @Builder.Default
    private String client = ClientEnum.WEB.name();

    @Builder.Default
    private String platform = PlatformEnum.BYTEDESK.name();

    // 修改邮箱、手机号需要记录用户uid和组织uid
    private String userUid;
}