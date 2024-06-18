/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-30 22:48:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-17 14:03:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

public enum WorkgroupRouteEnum {
    // Round-robin, 轮询分配. 根据客服上线时间进入队列轮流分配。注意：当客服网络不稳定掉线重连之后，会重新排到队列末尾
    ROBIN,
    // 当日内按照接待个数平均分配，少者优先, 0点清空前一天，并开始新的一天计数
    AVERAGE,
    // 当前饱和度，饱和度越低，空缺位置越多，优先分配
    IDLE,
    // 当前正在进行中对话数量，少者优先
    LESS,
    // 广播给所有客服，客服抢单
    BOARDCAST,
    // 熟客优先，最近会话优先分配
    RECENT,
    // TODO: 智能分配，还没有想清楚，待后续完善
    SMART,
    // TODO: 根据客服满意度评分等其他因素自动分配
    RATE,
    // 优先级分配
    PRIORITY,
    // 随机分配
    RANDOM,
}
