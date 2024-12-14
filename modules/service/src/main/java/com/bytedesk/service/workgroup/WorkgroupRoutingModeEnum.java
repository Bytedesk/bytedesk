/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-30 22:48:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-07 14:46:24
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

/**
 * 每种算法都有其适用场景：
 * 轮询：客服能力相近
 * 最小活动数：负载敏感
 * 随机：简单场景
 * 加权随机：重视服务质量
 * 一致性哈希：需要服务连续性
 * 最快响应：重视响应速度
 */
public enum WorkgroupRoutingModeEnum {
    // Round-robin, 轮询分配. 根据客服上线时间进入队列轮流分配。注意：当客服网络不稳定掉线重连之后，会重新排到队列末尾
    // 按顺序循环分配, 保证公平性,适合能力相近的客服
    ROUND_ROBIN,
    // 最小活动数算法, 选择当前会话最少的客服, 实现负载均衡, 避免单个客服过载
    LEAST_ACTIVE,
    // 随机分配, 完全随机选择, 实现简单 ,适合短期任务
    RANDOM,
    // 加权随机分配, 考虑客服评分和性能, 更好的客服获得更多机会, 提高整体服务质量
    WEIGHTED_RANDOM,
    // 一致性哈希分配, 相同访客尽量分配给同一客服, 提高服务连续性, 增加客户粘性
    CONSISTENT_HASH,
    // 最快响应时间, 选择响应最快的客服, 提高用户体验, 激励客服提高效率
    FASTEST_RESPONSE,

    
    // 当日内按照接待个数平均分配，少者优先, 0点清空前一天，并开始新的一天计数
    AVERAGE,
    // 当前饱和度，饱和度越低，空缺位置越多，优先分配
    IDLE,
    // 当前正在进行中对话数量，少者优先
    LESS,
    // 广播给所有客服，客服抢单
    BROADCAST,
    // 熟客优先，最近会话优先分配
    RECENT,
    // TODO: 智能分配，还没有想清楚，待后续完善
    // SMART,
    // TODO: 根据客服满意度评分等其他因素自动分配
    // RATE,
    // 优先级分配
    // PRIORITY,
}