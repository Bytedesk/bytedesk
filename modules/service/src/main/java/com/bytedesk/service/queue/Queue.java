/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:12:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-23 11:12:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.service.agent.Agent;
import com.bytedesk.service.visitor.Visitor;
import com.bytedesk.service.workgroup.Workgroup;

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

/**
 * 
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ QueueListener.class })
@Table(name = "service_queue")
public class Queue extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "queue_type")
    private String type;

    private Integer orderInQueue;

    private QueueStatusEnum status;

    // 排队访客
    @ManyToOne(fetch = FetchType.LAZY)
    private Visitor visitor;

    // 1. 被排队一对一客服
    // 2. 或技能组中待分配客服，通过type区分
    @ManyToOne(fetch = FetchType.LAZY)
    private Agent agent;

    // 1. 被排队技能组
    // 2. 或待分配客服所属技能组 通过type区分
    @ManyToOne(fetch = FetchType.LAZY)
    private Workgroup workgroup;

    /** belong to org */
    // @JsonIgnore
    // @ManyToOne(fetch = FetchType.LAZY)
    // private Organization organization;
    // private String orgUid;
}
