/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-30 11:31:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-30 11:32:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push.service;

import com.bytedesk.core.push_android_device.service.AliyunPushDeviceService;
import com.bytedesk.core.push_android_device.service.PushDeviceRouter;

/**
 * 华为厂商通道推送（历史空壳）
 *
 * @deprecated 华为通道已并入阿里云 EMAS Push 聚合接入（华为/小米/OPPO/vivo/荣耀/魅族/FCM 统一走
 *             {@link AliyunPushDeviceService}，按账号维度推送），不再单独对接 HMS Push SDK。
 *             保留类名仅为兼容既有引用；新代码请统一使用
 *             {@link PushDeviceRouter#pushToUser(String, String, String, java.util.Map)}。
 * @see PushDeviceRouter
 * @see AliyunPushDeviceService
 */
@Deprecated
public class PushHwService {

    public PushHwService() {
        // 路由桩：无独立实现，厂商通道统一走 AgentDevicePushRouter → AliyunDevicePushService
    }
}
