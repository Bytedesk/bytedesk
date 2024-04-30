/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 10:17:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-22 12:38:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.RouteConsts;
import com.bytedesk.core.utils.BaseRequest;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class WorkgroupRequest extends BaseRequest {

    private String wid;

    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.DEFAULT_WORK_GROUP_AVATAR_URL;

    @Builder.Default
    private String description = BdConstants.DEFAULT_WORK_GROUP_DESCRIPTION;

    @Builder.Default
    private String routeType = RouteConsts.ROUTE_TYPE_ROBIN;

    @Builder.Default
    private Boolean recent = false;

    @Builder.Default
    private Boolean autoPop = false;

    @Builder.Default
    private Boolean showTopTip = false;

    @Builder.Default
    private String topTip = BdConstants.DEFAULT_WORK_GROUP_DEFAULT_TOP_TIP;

    @Builder.Default
    private String welcomeTip = BdConstants.DEFAULT_WORK_GROUP_WELCOME_TIP;

    @Builder.Default
    private Boolean defaultRobot = false;

    @Builder.Default
    private Double autoCloseMin = Double.valueOf(5);

    // 注意：此处不能命名为agents，因与agent中agents类型不同, 否则会报错
    @Builder.Default
    private List<String> agentAids = new ArrayList<String>();

    // organization oid
    private String orgOid;
}
