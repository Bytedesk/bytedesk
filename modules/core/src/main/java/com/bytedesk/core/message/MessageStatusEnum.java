/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 10:33:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-02 11:24:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

public enum MessageStatusEnum {
    SENDING, // sending
    TIMEOUT, // network send failed
    BLOCKED, // in black list
    NOTFRIEND, // not friend
    ERROR, // other send error
    SUCCESS, // send success
    RECALL, // recall back
    UNPRECESSED, // leave message unprocessed
    DELIVERED, // send to the other client
    READ, // read by the other client
    DESTROYED, // destroyed after read
    PROCESSED, // leave message processed
    RATED, // robot/rate message rated
}
