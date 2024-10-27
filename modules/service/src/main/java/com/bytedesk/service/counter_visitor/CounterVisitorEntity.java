/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 09:58:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-22 12:19:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.counter_visitor;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 记录访客的号码
 * 按照技能组编号，技能组之间的号码无关联
 * 取号：每进入一个访客都需要取号
 * @Author: jackning 270580156@qq.com
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ CounterVisitorEntityListener.class })
@Table(name = "bytedesk_service_counter_visitor")
public class CounterVisitorEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String topic;
    
    // 计数器编号
    @Builder.Default
    private int serialNumber = 0;

    // 号码状态：初始状态、等待中、服务中、已完成
    @Builder.Default
    private String state = CounterVisitorStateEnum.INITIAL.name();    

    // 取号用户
    @Builder.Default
    @Column(name = "counter_visitor", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String visitor = BytedeskConsts.EMPTY_JSON_STRING;    
}
