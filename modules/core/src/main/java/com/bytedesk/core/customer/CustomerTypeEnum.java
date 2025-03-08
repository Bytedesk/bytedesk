/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 11:12:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-24 22:01:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.customer;

public enum CustomerTypeEnum {
    
    NEW_VISITOR("new_visitor", "新访客"),
    POTENTIAL("potential", "潜在客户"),
    FORMAL("formal", "正式客户"),
    LOST("lost", "流失客户");
    
    private String code;
    private String name;
    
    CustomerTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
}
