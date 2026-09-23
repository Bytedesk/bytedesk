/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-21
 * @Description: T0-3 存量修复结果项——记录 before/after 与跳过原因，供人工核对与审计
 */
package com.bytedesk.core.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 联系方式镜像修复结果项（修复仅覆盖 User 侧非空值，User 侧为空的字段跳过不清空）。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberContactFixResult {

    /** 成员 uid */
    private String memberUid;

    /** 所属组织 uid */
    private String orgUid;

    /** 关联平台用户 uid */
    private String userUid;

    /** 是否执行了修改 */
    private Boolean fixed;

    private String beforeMobile;
    private String afterMobile;

    private String beforeEmail;
    private String afterEmail;

    private String beforeCountry;
    private String afterCountry;

    /** 跳过/失败原因（如：成员不存在、已一致、User 侧手机号为空等） */
    private String message;
}
