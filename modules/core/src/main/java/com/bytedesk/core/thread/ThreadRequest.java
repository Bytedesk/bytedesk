/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-21 10:01:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-04 09:55:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ThreadRequest extends BaseRequest {

    // @NotBlank
    // private String title;
    // @NotBlank
    // private String avatar;
    
    // 
    private String topic;

    private ThreadStatusEnum status;

    private UserProtobuf user;
    
    private String userNickname;

    private String ownerNickname;

    // 
    @Builder.Default
    private Boolean top = false;

    @Builder.Default
    private Boolean unread = false;

    @Builder.Default
    private Integer unreadCount = 0;

    @Builder.Default
    private Boolean mute = false;

    @Builder.Default
    private boolean hide = false;

    @Builder.Default
    private Integer star = 0;

    @Builder.Default
    private Boolean folded = false;

    // group member uids
    @Builder.Default
    private List<String> memberUids = new ArrayList<>();
}
