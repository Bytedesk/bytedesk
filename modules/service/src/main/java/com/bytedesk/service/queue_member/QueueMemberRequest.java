/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 17:57:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-07 16:04:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.time.LocalDateTime;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.DatabaseTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueueMemberRequest extends BaseRequest {

    private String queueUid;
    
    @Builder.Default
    private int currentNumber = 1;

    @Builder.Default
    private int waitingNumber = 0;

    @Builder.Default
    private int waitSeconds = 0;
    
    @Builder.Default
    private String state = QueueMemberStatusEnum.WAITING.name();   

    private String threadTopic;
    
    @Builder.Default
    private String visitor = BytedeskConsts.EMPTY_JSON_STRING;    

    private LocalDateTime acceptedAt;

    private String userUid;
    
    @Builder.Default
    private String dbType = DatabaseTypeEnum.MYSQL.name();
}
