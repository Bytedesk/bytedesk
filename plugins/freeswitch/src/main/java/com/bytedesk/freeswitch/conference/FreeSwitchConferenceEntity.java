/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 19:50:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.conference;

import com.bytedesk.core.base.BaseEntity;

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
import lombok.experimental.SuperBuilder;

/**
 * FreeSwitch会议室实体
 * 对应数据库表：freeswitch_conferences
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({FreeSwitchConferenceEntityListener.class})
@Table(name = "bytedesk_freeswitch_conference")
public class FreeSwitchConferenceEntity extends BaseEntity {

    /**
     * 会议室名称
     */
    @Column(unique = true)
    private String conferenceName;

    /**
     * 会议室描述
     */
    @Column
    private String description;

    /**
     * 会议室密码
     */
    @Column
    private String password;

    /**
     * 最大参与者数量
     */
    @Column
    private Integer maxMembers;

    /**
     * 是否启用
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 是否录音
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean recordEnabled = false;

    /**
     * 录音文件路径
     */
    @Column(length = 500)
    private String recordPath;

    /**
     * 创建者
     */
    @Column(length = 100)
    private String creator;

    /**
     * 会议室配置参数（JSON格式）
     */
    @Column(columnDefinition = "TEXT")
    private String configJson;

    /**
     * 备注
     */
    @Column(length = 500)
    private String remarks;

    /**
     * 检查会议室是否有密码保护
     */
    public boolean isPasswordProtected() {
        return password != null && !password.trim().isEmpty();
    }

    /**
     * 检查会议室是否已满
     */
    public boolean isFull(int currentMembers) {
        return maxMembers != null && currentMembers >= maxMembers;
    }
}
