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
package com.bytedesk.team.group;

import java.util.List;

// import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.team.member.MemberProtobuf;

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

    private String name;

    private String avatar;

    private String description;

    private Boolean showTopTip;

    private String topTip;

    private GroupTypeEnum type;

    private GroupStatusEnum status;

    private Boolean isExternal;

    // 替代直接返回所有成员列表，增加成员计数
    private Integer memberCount;

    // 当需要返回部分成员时（如前10个）使用
    private List<MemberProtobuf> memberPreview;
    
    private List<UserProtobuf> admins;

    // private List<RobotProtobuf> robots;

    private UserProtobuf creator;

}
