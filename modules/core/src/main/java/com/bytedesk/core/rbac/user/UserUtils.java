/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-31 16:20:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-22 17:44:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;

public class UserUtils {
    
    public static UserProtobuf getFileAssistantUser() {
        UserProtobuf user = UserProtobuf.builder()
                .nickname(I18Consts.I18N_FILE_ASSISTANT_NAME)
                .avatar(AvatarConsts.DEFAULT_FILE_ASSISTANT_AVATAR_URL)
                .build();
        user.setUid(BytedeskConsts.DEFAULT_FILE_ASSISTANT_UID);
        return user;
    }

    public static UserProtobuf getClipboardAssistantUser() {
        UserProtobuf user = UserProtobuf.builder()
                .nickname(I18Consts.I18N_CLIPBOARD_ASSISTANT_NAME)
                .avatar(AvatarConsts.DEFAULT_CLIPBOARD_ASSISTANT_AVATAR_URL)
                .build();
        user.setUid(BytedeskConsts.DEFAULT_CLIPBOARD_ASSISTANT_UID);
        return user;
    }

    public static UserProtobuf getSystemChannelUser() {
        UserProtobuf user = UserProtobuf.builder()
                .nickname(I18Consts.I18N_SYSTEM_NOTIFICATION_NAME)
                .avatar(AvatarConsts.DEFAULT_SYSTEM_NOTIFICATION_AVATAR_URL)
                .build();
        user.setUid(BytedeskConsts.DEFAULT_SYSTEM_UID);
        return user;
    }

}
