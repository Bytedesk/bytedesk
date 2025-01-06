/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 11:53:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 12:06:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.forum.user;

import lombok.Data;

@Data
public class ForumUserStats {
    private Long userId;
    private Integer postCount = 0;
    private Integer commentCount = 0;
    private Integer likeCount = 0;
} 