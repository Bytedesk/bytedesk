/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-19 00:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-19 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.webrtc.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 会议加入结果：房间信息 + 当前登录用户展示信息 + Janus 可用性
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RoomJoinResponse {

    /**
     * 会议室信息
     */
    private RoomResponse room;

    /**
     * 当前登录用户展示名（昵称优先，回退 uid），前端作为 AudioBridge join 的 display
     */
    private String displayName;

    /**
     * 当前登录用户 uid
     */
    private String userUid;

    /**
     * 会议室创建者（主持人）uid，仅用于前端展示录制按钮等界面提示；
     * 录制等写操作仍须由后端再次校验，不能以此字段作为授权依据
     */
    private String hostUid;

    /**
     * bytedesk.webrtc.janus.enabled 是否开启
     */
    private Boolean janusEnabled;

    /**
     * 会议容量（= RoomEntity.maxParticipants，历史房间缺省时回退默认值），
     * 前端创建 Janus videoroom 房间时作为 publishers 参数使用
     */
    private Integer publisherLimit;
}
