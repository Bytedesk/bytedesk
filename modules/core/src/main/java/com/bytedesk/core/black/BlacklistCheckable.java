/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-08 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.rbac.user.UserProtobuf;

/**
 * 黑名单校验感知接口
 *
 * 访客侧写操作请求（如创建工单、提交留言、提交表单、上传文件等）实现此接口，
 * 供 BlackUserAspect 在方法执行前提取操作者（访客/用户）uid，
 * 结合请求中的 orgUid 进行黑名单拦截，禁止被拉黑访客继续操作。
 *
 * 注意：无法确定操作者身份时返回 null，此时跳过用户黑名单校验，由 IP 黑名单兜底。
 */
public interface BlacklistCheckable {

    /**
     * 返回发起当前操作的访客/用户 uid；无法确定时返回 null
     */
    String getBlacklistOperatorUid();

    /**
     * 从 user JSON 字符串（UserProtobuf/VisitorProtobuf 序列化）中提取 uid，
     * 供实现类复用；解析失败或无 uid 时返回 null
     */
    static String extractUidFromUserJson(String userJson) {
        if (!StringUtils.hasText(userJson)) {
            return null;
        }
        try {
            UserProtobuf userProtobuf = JSON.parseObject(userJson, UserProtobuf.class);
            return userProtobuf != null && StringUtils.hasText(userProtobuf.getUid())
                    ? userProtobuf.getUid()
                    : null;
        } catch (Exception e) {
            // 解析失败时忽略，交由 IP 黑名单兜底
            return null;
        }
    }
}
