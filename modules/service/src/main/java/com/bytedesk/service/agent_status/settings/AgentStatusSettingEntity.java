/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 11:45:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status.settings;

import org.modelmapper.ModelMapper;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
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

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({AgentStatusSettingEntityListener.class})
@Table(name = "bytedesk_service_agent_status_setting")
public class AgentStatusSettingEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String name;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    // @Builder.Default
    // @Column(name = "tag_type")
    // private String type = AgentStatusSettingTypeEnum.CUSTOMER.name();

    // 是否需要开启客服切换状态审核
    @Builder.Default
    @Column(name = "need_review")
    private Boolean needReview = false;
    
    // 审核时间段类型：ANY_TIME(任意时间)或CUSTOM_TIME(自定义时间)
    @Builder.Default
    @Column(name = "review_time_type")
    private String reviewTimeType = "ANY_TIME";
    
    // 自定义审核开始时间，格式如：08:30
    @Column(name = "review_start_time")
    private String reviewStartTime;
    
    // 自定义审核结束时间，格式如：17:30
    @Column(name = "review_end_time")
    private String reviewEndTime;
    
    // 审核方式：ALWAYS_MANUAL(始终等待人工审核)、AUTO_APPROVE(超时自动通过)、AUTO_REJECT(超时自动拒绝)
    @Builder.Default
    @Column(name = "review_method")
    private String reviewMethod = "ALWAYS_MANUAL";
    
    // 审核超时时间（分钟）
    @Builder.Default
    @Column(name = "review_timeout_minutes")
    private Integer reviewTimeoutMinutes = 10;

    /**
     * 从 AgentStatusSettingRequest 创建 AgentStatusSettingEntity 实体
     * 如果 request 为 null，返回默认构建的实体
     *
     * @param request AgentStatusSettingRequest 对象，可以为 null
     * @param modelMapper ModelMapper 实例用于字段映射
     * @return AgentStatusSettingEntity 实体，永远不为 null
     */
    public static AgentStatusSettingEntity fromRequest(AgentStatusSettingRequest request, ModelMapper modelMapper) {
        if (request == null) {
            return AgentStatusSettingEntity.builder().build();
        }
        return modelMapper.map(request, AgentStatusSettingEntity.class);
    }
}
