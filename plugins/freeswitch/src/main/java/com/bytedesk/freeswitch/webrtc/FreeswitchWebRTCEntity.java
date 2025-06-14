/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-14 10:14:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.webrtc;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FreeSwitch 视频客服实体
 * 用于管理基于WebRTC的视频客服功能
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({FreeswitchWebRTCEntityListener.class})
@Table(name = "bytedesk_freeswitch_webrtc")
public class FreeswitchWebRTCEntity extends BaseEntity {

    /**
     * 客服名称
     */
    @Column(unique = true)
    private String name;

    /**
     * 客服描述
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * WebRTC类型
     * CUSTOMER: 客服
     * AGENT: 坐席
     * QUEUE: 队列
     */
    @Builder.Default
    @Column(name = "webrtc_type")
    private String type = FreeswitchWebRTCTypeEnum.CUSTOMER.name();

    /**
     * 客服状态
     * ONLINE: 在线
     * OFFLINE: 离线
     * BUSY: 忙碌
     */
    @Builder.Default
    @Column(name = "status")
    private String status = "OFFLINE";

    /**
     * 是否启用
     */
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    /**
     * 最大并发视频数
     */
    @Builder.Default
    private Integer maxVideoCalls = 1;

    /**
     * 视频质量
     * HIGH: 高清
     * MEDIUM: 标清
     * LOW: 流畅
     */
    @Builder.Default
    private String videoQuality = "HIGH";

    /**
     * 是否允许录音
     */
    @Builder.Default
    @Column(name = "is_record_enabled")
    private Boolean recordEnabled = false;

    /**
     * 录音文件路径
     */
    private String recordPath;

    /**
     * 客服分组
     */
    private String groupName;

    /**
     * 客服技能标签
     * 多个标签用逗号分隔
     */
    private String skillTags;

    /**
     * 工作时间
     * JSON格式，例如：
     * {
     *   "monday": {"start": "09:00", "end": "18:00"},
     *   "tuesday": {"start": "09:00", "end": "18:00"}
     * }
     */
    @Column(name = "working_hours", columnDefinition = "TEXT")
    private String workingHours;

    /**
     * 扩展配置
     * JSON格式，用于存储其他配置信息
     */
    @Column(name = "config_json", columnDefinition = "TEXT")
    private String configJson;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 检查客服是否在线
     */
    public boolean isOnline() {
        return "ONLINE".equalsIgnoreCase(status) && enabled;
    }

    /**
     * 检查客服是否忙碌
     */
    public boolean isBusy() {
        return "BUSY".equalsIgnoreCase(status);
    }

    /**
     * 检查是否在工作时间内
     */
    public boolean isInWorkingHours() {
        // TODO: 实现工作时间检查逻辑
        return true;
    }

    /**
     * 获取客服技能标签列表
     */
    public List<String> getSkillTagList() {
        if (skillTags == null || skillTags.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(skillTags.split(","));
    }
}
