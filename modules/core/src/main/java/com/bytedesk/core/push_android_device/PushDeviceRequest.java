/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-21 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-22 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms of the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push_android_device;

import com.bytedesk.core.base.BaseRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 设备推送绑定/解绑/管理请求
 *
 * <p>移动端登录后上报：provider=aliyun + account=userUid + deviceId（阿里云 SDK 分配）。
 * 真实的账号绑定由移动端 aliyun_push.bindAccount(userUid) 在阿里云侧完成，
 * bind/unbind 接口仅维护后端对账记录（候选池判定 + 推送漏斗观测）；
 * query/create/update/delete 供管理后台维护绑定信息。
 *
 * <p>注意：type/channel 字段由 BaseRequest 提供，此处不重复声明。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class PushDeviceRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /** 推送供应商：aliyun（预留扩展） */
    private String provider;

    /** 供应商侧设备标识（阿里云 deviceId，对账/排障用） */
    private String deviceId;

    /** 绑定账号 = userUid（bind 场景须与当前登录用户一致，服务端强制校验） */
    private String account;

    /** 设备平台：ANDROID / IOS */
    private String device;

    /** 测试推送：通知标题（仅 /test 接口使用） */
    private String title;
}
