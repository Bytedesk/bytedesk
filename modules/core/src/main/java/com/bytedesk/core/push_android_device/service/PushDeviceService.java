/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-21 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-21 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push_android_device.service;

import java.util.Map;

/**
 * 设备推送供应商门面（坐席侧业务通知：会话邀请、工单状态变更等）
 *
 * <p>设计要点（对齐规划文档 docs/plans/2026-09-21-android-vendor-channel-push-plan.md §4.3）：
 * <ul>
 * <li>供应商抽象，不直接绑定 APNs 或阿里云；按 userUid（账号维度）推送，天然支持一账号多设备；</li>
 * <li>实现分两类：
 *   {@link PushApnsDeviceService}（封装现有 PushApnsService 直连 APNs，社区版/私有化兜底）、
 *   {@link AliyunPushDeviceService}（阿里云移动推送 EMAS，Android 厂商通道统一聚合）；</li>
 * <li>切换开关：bytedesk.push.provider=aliyun|apns-direct，由 {@link PushDeviceRouter} 解析并回退；</li>
 * <li>所有业务触达点（路由策略、工单事件等）只依赖本接口，不感知具体供应商。</li>
 * </ul>
 */
public interface PushDeviceService {

    /** 供应商标识：aliyun / apns-direct */
    String getProviderName();

    /**
     * 供应商是否可用（凭据已配置、通道已启用）。
     * 不可用时路由器应回退到其它供应商，避免分配了通知不到的坐席。
     */
    boolean isAvailable();

    /**
     * 按账号（userUid）推送业务通知。
     *
     * @param userUid 接收者用户 uid（阿里云 Target=ACCOUNT / APNs 按 token 归属用户）
     * @param title   通知标题
     * @param body    通知正文
     * @param extras  业务扩展字段（threadUid / ticketUid / type 等），供应商透传到通知 payload 供点击跳转
     * @return 推送结果（成功/失败 + 供应商侧消息 id）
     */
    PushDeviceResult pushToUser(String userUid, String title, String body, Map<String, String> extras);
}
