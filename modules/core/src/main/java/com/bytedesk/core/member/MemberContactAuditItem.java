/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-21
 * @Description: T0-3 存量盘点结果项——Member 与关联 User 联系方式不一致的只读审计记录
 */
package com.bytedesk.core.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 联系方式镜像一致性盘点项（dry-run 输出，不做任何修改）。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberContactAuditItem {

    /** 成员 uid */
    private String memberUid;

    /** 所属组织 uid */
    private String orgUid;

    /** 关联平台用户 uid */
    private String userUid;

    private String memberMobile;
    private String userMobile;

    private String memberEmail;
    private String userEmail;

    private String memberCountry;
    private String userCountry;
}
