/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 08:54:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 21:37:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.fixed;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.message.MessageTypeEnum;

public class AutoReplyFixedInitData {

    /**
     * Default category name for auto reply
     * 默认自动回复分类名称
     */
    public static final String DEFAULT_CATEGORY_NAME = "默认分类";

    /**
     * Auto reply data structure
     * 自动回复数据结构
     */
    public static class AutoReplyData {
        private String content;
        private String type;
        private boolean enabled;

        public AutoReplyData(String content, String type, boolean enabled) {
            this.content = content;
            this.type = type;
            this.enabled = enabled;
        }

        public String getContent() {
            return content;
        }

        public String getType() {
            return type;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    /**
     * Get default auto reply data list
     * 获取默认自动回复数据列表
     * 
     * @return List of AutoReplyData
     */
    public static List<AutoReplyData> getDefaultAutoReplyData() {
        List<AutoReplyData> autoReplyDataList = new ArrayList<>();
        
        // 欢迎消息
        autoReplyDataList.add(new AutoReplyData(
            "您好！欢迎使用我们的客服系统，请问有什么可以帮助您的吗？",
            MessageTypeEnum.TEXT.name(),
            true
        ));

        // 工作时间消息
        autoReplyDataList.add(new AutoReplyData(
            "我们的工作时间是周一至周五 9:00-18:00，周末和节假日休息。如有紧急问题，请留言，我们会在下一个工作日尽快回复您。",
            MessageTypeEnum.TEXT.name(),
            true
        ));

        // 联系方式消息
        autoReplyDataList.add(new AutoReplyData(
            "您可以通过以下方式联系我们：\n1. 客服热线：400-123-4567\n2. 邮箱：support@example.com\n3. 在线客服：工作时间在线",
            MessageTypeEnum.TEXT.name(),
            true
        ));

        return autoReplyDataList;
    }


}