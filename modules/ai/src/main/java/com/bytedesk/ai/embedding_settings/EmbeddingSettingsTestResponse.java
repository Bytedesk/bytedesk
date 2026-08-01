/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-04 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-07-04 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.embedding_settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingSettingsTestResponse {

    private Boolean success;

    private String message;

    public static EmbeddingSettingsTestResponse success(String message) {
        return EmbeddingSettingsTestResponse.builder()
                .success(true)
                .message(message)
                .build();
    }

    public static EmbeddingSettingsTestResponse fail(String message) {
        return EmbeddingSettingsTestResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
