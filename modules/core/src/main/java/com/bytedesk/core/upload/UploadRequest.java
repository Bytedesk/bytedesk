/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-18 12:06:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 16:38:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UploadRequest extends BaseRequest {

    private String nickname;

    private String fileName;

    private String fileSize;

    private String fileUrl;

    private String fileType;

    private String client;

    private String user;

    private UploadStatusEnum status;

    private String categoryUid; // 所属分类

    private String kbUid; // 所属知识库

    private String kbType;     // 知识库类型
    
    private Boolean isAvatar;  // 是否为头像
    // 
    private String visitorUid; // 游客ID
    private String visitorNickname; // 游客昵称
    private String visitorAvatar; // 游客头像
    // 
    private String extra; // 额外附加信息
}
