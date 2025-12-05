/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-28 12:06:13
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-02-17 16:47:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message.utils;

import java.io.IOException;

import com.bytedesk.core.socket.protobuf.model.MessageProto;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageConvertUtils {
    
    private MessageConvertUtils() {
    }

    public static String toJson(Message sourceMessage) throws IOException {
        return JsonFormat.printer().print(sourceMessage);
    }

    public static MessageProto.Message toProtoBean(MessageProto.Message.Builder targetBuilder, String json)
            throws IOException {
        // JsonFormat.parser().merge(json, targetBuilder);
        // return targetBuilder.build();
        try {
            // Use the builder pattern correctly
            JsonFormat.parser().ignoringUnknownFields().merge(json, targetBuilder);
            return targetBuilder.build();
        } catch (Exception e) {
            log.error("Error converting JSON to Proto: ", e);
            return null;
        }
    }

}
