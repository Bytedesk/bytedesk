/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.io.Serializable;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活跃会话缓存数据结构
 * 用于在 Redis 中缓存活跃服务会话的关键信息，避免频繁查询数据库
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveThreadCache implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 会话 UID */
    private String uid;

    /** 会话 Topic */
    private String topic;

    /** 会话类型: AGENT, WORKGROUP, ROBOT */
    private String type;

    /** 会话状态 */
    private String status;

    /** 会话 extra（包含 ServiceSettings 配置） */
    private String extra;

    /** 最后更新时间戳（毫秒） */
    private Long updatedAtMillis;

    /** 组织 UID */
    private String orgUid;

    /**
     * 从 ThreadEntity 创建缓存对象
     */
    public static ActiveThreadCache fromThread(ThreadEntity thread) {
        return ActiveThreadCache.builder()
                .uid(thread.getUid())
                .topic(thread.getTopic())
                .type(thread.getType())
                .status(thread.getStatus())
                .extra(thread.getExtra())
                .updatedAtMillis(BdDateUtils.toTimestamp(thread.getUpdatedAt()))
                .orgUid(thread.getOrgUid())
                .build();
    }

    /**
     * 转换为 JSON 字符串
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }

    /**
     * 从 JSON 字符串解析
     */
    public static ActiveThreadCache fromJson(String json) {
        return JSON.parseObject(json, ActiveThreadCache.class);
    }

    /**
     * 更新最后更新时间
     */
    public void updateTimestamp() {
        this.updatedAtMillis = BdDateUtils.toTimestamp(BdDateUtils.now());
    }

    /**
     * 更新状态
     */
    public void updateStatus(String status) {
        this.status = status;
        updateTimestamp();
    }
}
