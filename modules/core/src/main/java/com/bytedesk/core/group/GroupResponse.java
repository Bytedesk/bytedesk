/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 09:55:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-07 13:29:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group;

import java.util.List;

// import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.group.enums.GroupStatusEnum;
import com.bytedesk.core.group.enums.GroupTypeEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.member.MemberProtobuf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GroupResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;


    private String name;

    private String avatar;

    private String description;

    private Boolean showTopTip;

    private String topTip;

    private GroupTypeEnum type;

    private GroupStatusEnum status;

    private Boolean isExternal;

    /** 群可见性：PUBLIC / PRIVATE */
    private String visibility;

    /** 公开群加入是否需要审批 */
    private Boolean needApproval;

    /** 当前请求用户是否已是成员（公开搜索列表用） */
    private Boolean joined;

    /** 当前请求用户是否有待审批申请（公开搜索列表用） */
    private Boolean pendingApply;

    // 替代直接返回所有成员列表，增加成员计数
    private Integer memberCount;

    // 当需要返回部分成员时（如前10个）使用
    private List<MemberProtobuf> memberPreview;
    
    private List<UserProtobuf> admins;

    // 群机器人UID列表（机器人详情由前端通过 /api/v1/robot/query/uids 批量获取）
    private List<String> robotUids;

    private UserProtobuf creator;

}
