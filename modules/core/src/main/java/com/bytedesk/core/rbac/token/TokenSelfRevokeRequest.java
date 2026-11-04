package com.bytedesk.core.rbac.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 撤销本人其他登录会话的请求。仅允许携带目标 uid 与操作来源 channel；
 * userUid/orgUid 等归属字段一律以服务端认证上下文为准，不接受客户端提交。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenSelfRevokeRequest {

    private String uid;

    /** 发起操作的客户端渠道，仅用于审计，不用于过滤 */
    private String channel;
}
