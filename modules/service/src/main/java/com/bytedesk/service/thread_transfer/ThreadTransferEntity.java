/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:25:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 06:46:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.thread_transfer;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageStatusEnum;

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
 * 记录转接记录：
 * 人工转接人工
 * 机器人转人工
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_thread_transfer")
public class ThreadTransferEntity extends BaseEntity {

    @Builder.Default
    // json字段格式，搜索时，对数据库有依赖，不方便迁移
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // @JdbcTypeCode(SqlTypes.JSON)
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String sender = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    // json字段格式，搜索时，对数据库有依赖，不方便迁移
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // @JdbcTypeCode(SqlTypes.JSON)
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String receiver = BytedeskConsts.EMPTY_JSON_STRING;

    private String note;

    @Builder.Default
    // @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type", nullable = false)
    private String type = ThreadTransferTypeEnum.ROBOT_TO_AGENT.name();

    @Builder.Default
    @Column(name = "transfer_status", nullable = false)
    private String status = MessageStatusEnum.SUCCESS.name();

    private String threadTopic;

}
