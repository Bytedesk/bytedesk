/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 10:01:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.constant;

public class TypeConsts {

    private TypeConsts() {
    }

    public static final String TYPE_SYSTEM = "system";

    /**
     * region类型, 代码长度分别为：省 2、市 4、区/县 6、镇 9
     */
    public static final String REGION_TYPE_PROVINCE = "province";
    public static final String REGION_TYPE_CITY = "city";
    public static final String REGION_TYPE_COUNTY = "county";
    public static final String REGION_TYPE_TOWN = "town";
    //
    public static final String COLUMN_TYPE_TEXT = "TEXT"; // length = 65534
    public static final String COLUMN_TYPE_JSON = "json"; // replace with jsonb?

    // 
    public static final String COMPONENT_TYPE_TEAM = "team";
    public static final String COMPONENT_TYPE_SERVICE = "service";
    public static final String COMPONENT_TYPE_ROBOT = "robot";
    public static final String COMPONENT_TYPE_VISITOR = "visitor";
    public static final String COMPONENT_TYPE_CHANNEL = "channel"; // 通道类型，比如：whatsapp, wechat, facebook, twitter, etc.

}
