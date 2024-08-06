/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-18 11:49:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-05 21:30:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.leave_msg;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class LeaveMsgExtra {
    private String uid;
    private String contact;
    private String content;
    private List<String> images = new ArrayList<>();
    private String orgUid;
}
