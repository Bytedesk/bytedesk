/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-26 12:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-26 12:20:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.constant;

/**
 *
 * @author xiaper.io
 */
public class RouteConsts {

    // Prevents instantiation
    private RouteConsts() {
    }

    /**
     * 轮询分配
     * Round-robin
     */
    public static final String ROUTE_TYPE_ROBIN = "robin";
    /**
     * 当日内按照接待个数平均分配，少者优先
     * 0：点清空前一天，并开始新的一天计数
     */
    public static final String ROUTE_TYPE_AVERAGE = "average";
    /**
     * 当前饱和度，饱和度越低，空缺位置越多，优先分配
     */
    public static final String ROUTE_TYPE_IDLE = "idle";
    /**
     * 当前正在进行中对话数量，少者优先
     */
    public static final String ROUTE_TYPE_LESS = "less";
    /**
     * 广播给所有客服，客服抢单
     */
    public static final String ROUTE_TYPE_BOARDCAST = "broadcast";
    /**
     * 熟客优先，最近会话优先分配
     */
    // public static final String ROUTE_TYPE_RECENT = "recent";
    /**
     * TODO: 智能分配，还没有想清楚，待后续完善
     */
    // public static final String ROUTE_TYPE_SMART = "smart";
    /**
     * TODO: 根据客服满意度评分等其他因素自动分配
     */
    // public static final String ROUTE_TYPE_RATE = "rate";

}
