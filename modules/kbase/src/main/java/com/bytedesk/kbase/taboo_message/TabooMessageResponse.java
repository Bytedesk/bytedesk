/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:35:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-02 08:24:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo_message;

import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.experimental.Accessors;

@Getter
@Setter
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class TabooMessageResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String content;

    private String messageUid;

    private String threadUid;

    private String replacedContent;

    private List<String> tabooWordList;

}
