/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-18 12:06:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 16:24:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.enums.ClientEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponse extends BaseResponse {

    private String fileName;

    private String fileSize;

    private String fileUrl;

    private String fileType;

    private ClientEnum client;

    private UploadTypeEnum type;

    private UploadStatusEnum status;

    private String categoryUid; // 所属分类

    private String kbUid; // 所属知识库

    private String user;

    private List<String> docIdList;

    private String extra;
}
