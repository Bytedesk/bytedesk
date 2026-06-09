/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:35:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push.apns_push;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.push.PushStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * APNS push delivery record.
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({ApnsPushEntityListener.class})
@Table(name = "bytedesk_core_apns_push")
public class ApnsPushEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** Push title shown in notification center. */
    private String name;

    /** Sender display name / uid. */
    private String sender;

    /** Receiver user uid. */
    private String receiver;

    /** Receiver device token. */
    @Column(name = "device_token", length = 512)
    private String deviceToken;

    /** Bound APNS certificate uid. */
    @Column(name = "p12_uid")
    private String p12Uid;

    /** APNS bundle identifier used for this push. */
    @Column(name = "bundle_id")
    private String bundleId;

    /** Message uid that triggered this push. */
    @Column(name = "message_uid")
    private String messageUid;

    /** Thread uid for the message conversation. */
    @Column(name = "thread_uid")
    private String threadUid;

    /** Serialized message content used in APNS payload. */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    /** Additional note / failure reason. */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String description;

    /** Record type / business source. */
    @Builder.Default
    @Column(name = "apns_push_type")
    private String type = ApnsPushTypeEnum.MESSAGE.name();

    @Builder.Default
    @Column(name = "push_status")
    private String status = PushStatusEnum.PENDING.name();

    @Builder.Default
    private String channel = ChannelEnum.IOS.name();

    private Boolean sandbox;

    private Boolean sendSuccess;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String sendMessage;

}
