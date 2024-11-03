/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-30 11:18:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRequest extends BaseRequest {

    private String title;

    private String summary;
    // private String coverImageUrl;
    
    private String contentMarkdown;

    private String contentHtml;

    // search.html 搜索用
    private String content;

    // @Builder.Default
    // private MessageTypeEnum contentType = MessageTypeEnum.TEXT;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private boolean top = false;

    @Builder.Default
    private boolean published = false;

    @Builder.Default
    private boolean markdown = false;

    @Builder.Default
    private int readCount = 0;

    private String categoryUid;

    private String kbUid;
}
