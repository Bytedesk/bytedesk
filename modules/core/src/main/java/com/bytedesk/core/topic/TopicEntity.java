/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-13 16:03:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-05 09:40:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import java.util.Set;
import java.util.HashSet;

import com.bytedesk.core.base.BaseEntityNoOrg;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringSetConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_topic")
public class TopicEntity extends BaseEntityNoOrg {

    private static final long serialVersionUID = 1L;

    /** 为防止后添加的记录，clientIds缺失，所以用数组代替，这样每个用户在topic中只有一条记录，clientIds可共用 */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    @Convert(converter = StringSetConverter.class)
    private Set<String> topics = new HashSet<>();

    /** AT_MOST_ONCE(0),AT_LEAST_ONCE(1), EXACTLY_ONCE(2), */
    @Builder.Default
    private Integer qos = 1;

    /**
     * current online clientIds
     * 用户clientId格式: uid/client/deviceUid
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    @Convert(converter = StringSetConverter.class)
    private Set<String> clientIds = new HashSet<>();

}
