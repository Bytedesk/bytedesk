/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 10:26:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 10:24:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

/**
 * 客服状态枚举类
 */
public enum AgentStatusEnum {
    AVAILABLE, // 在线/接待状态
    // AVAILABLE_AUDIO, // 支持音频会话，修改为可勾选
    // AVAILABLE_VIDEO, // 支持视频会话，修改为可勾选
    // AVAILABLE_PHONE, // 支持电话会话，修改为可勾选
    // AWAY, // 离开状态，合并到REST状态中，支持设置离开原因
    AFTER_CALL, // 话后整理：在接听电话结束时，状态将由忙碌变为话后整理并进入话后整理倒计时，倒计时结束员工自动进入空闲状态。倒计时结束前员工可单击返回接待进入空闲状态，或单击继续话后整理保持该状态。
    REST, // 小休/休息状态： 支持设置小休原因，后台可配置，可设置多个，支持前端用户选择
    BUSY, // 忙碌/挂起状态
    OFFLINE; // 下线/离线状态

    // 根据字符串查找对应的枚举常量
    public static AgentStatusEnum fromValue(String value) {
        for (AgentStatusEnum type : AgentStatusEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No AgentStatus constant with value: " + value);
    }
}
