/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-15 17:15:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-15 17:15:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

/**
 * 用于在线程间传递敏感词过滤相关的上下文
 */
public class TabooContext {
    private static final ThreadLocal<Boolean> MESSAGE_BLOCKED = new ThreadLocal<>();
    
    /**
     * 设置当前消息是否应该被拦截
     * @param blocked 是否阻止消息发送
     */
    public static void setMessageBlocked(boolean blocked) {
        MESSAGE_BLOCKED.set(blocked);
    }
    
    /**
     * 检查当前消息是否应该被拦截
     * @return true 如果消息需要被拦截
     */
    public static boolean isMessageBlocked() {
        Boolean blocked = MESSAGE_BLOCKED.get();
        return blocked != null &amp;amp;& blocked;
    }
    
    /**
     * 清除上下文
     * 在处理完毕后应调用此方法避免内存泄漏
     */
    public static void clear() {
        MESSAGE_BLOCKED.remove();
    }
}
