/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-20 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_feishu;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class FeishuDocRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String feishuUid;

    private String kbUid;

    private String categoryUid;

    /** WIKI_NODE / DRIVE_FILE */
    private String resourceType;

    private String spaceId;

    private String nodeToken;

    private String folderToken;

    private String objToken;

    private String objType;

    private String title;

    private String content;

    private String sourceUrl;

    private Long objCreateTime;

    private Long objEditTime;

    /** 规范化正文 SHA-256（由同步侧计算） */
    private String contentHash;

    private Boolean enabled;

    private String orgUid;
}
