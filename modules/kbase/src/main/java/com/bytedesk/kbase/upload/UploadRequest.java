/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-18 12:06:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-30 22:27:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.upload;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadRequest extends BaseRequest {

    private String fileName;

    private String fileSize;

    private String fileUrl;

    private String fileType;

    private String client;

    private String user;

    // 大模型
    private String loader;

    private String splitter;

    private int docsCount;

    // private boolean isLlm;
    // private UploadTypeEnum type;

    private UploadStatusEnum status;

    private String categoryUid; // 所属分类

    private String kbUid; // 所属知识库
}
