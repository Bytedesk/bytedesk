/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 09:37:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-28 11:24:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.group;

// 群组类型
// 普通群、付费群、公开群、私有群、临时群、机器人群
// 普通群：普通群组，没有特殊功能
// 付费群：用户到期时间
// 公开群：所有人可以加入
// 私有群：需要邀请才能加入
// 临时群：临时群组，没有特殊功能
// 机器人群：机器人群组，没有特殊功能
public enum GroupTypeEnum {
    NORMAL, // 普通群组
    TOPIC; // 话题群组

    // 根据字符串查找对应的枚举常量
    public static GroupTypeEnum fromValue(String value) {
        for (GroupTypeEnum type : GroupTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No GroupTypeEnum constant with value: " + value);
    }
}
