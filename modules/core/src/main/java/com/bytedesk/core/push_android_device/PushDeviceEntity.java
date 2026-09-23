/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-22 10:00:00
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

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 设备推送绑定实体（对账镜像）
 *
 * <p>阿里云账号维度推送下，真实绑定在阿里云侧由移动端 bindAccount(userUid) 完成，
 * 本实体仅记录「userUid ↔ 供应商 deviceId」的绑定关系，供「后台候选坐席是否
 * 具备可用推送通道」判定与推送漏斗观测使用（由 PushDeviceRestService 维护）。
 *
 * <p>历史：由 PushTokenEntity（表 bytedesk_core_push_token，离线推送 token 语义）
 * 重构而来——旧实体的 token/name 列被复用承载 deviceId/账号导致语义错位，
 * 且其离线 token 原始用途已无消费方，故直接重命名并迁入 push_device 包。
 * 旧表成为无实体映射的遗留表，历史数据靠客户端幂等重报补齐，不迁移。
 *
 * <p>见 docs/plans/2026-09-22-push-device-binding-entity-plan.md
 */
@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_push_device", indexes = {
        @Index(name = "idx_push_device_uid", columnList = "uuid"),
        @Index(name = "idx_push_device_user_uid", columnList = "user_uid"),
        @Index(name = "idx_push_device_device_id", columnList = "device_id")
})
public class PushDeviceEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 推送供应商：aliyun（预留扩展，本期仅 aliyun） */
    @Column(name = "provider", length = 32)
    private String provider;

    /** 供应商侧设备标识（阿里云 deviceId，对账/排障用，非推送寻址主键） */
    @Column(name = "device_id", length = 128)
    private String deviceId;

    /** 绑定账号 = userUid（写时与 BaseEntity.userUid 强一致，便于运维直接检索） */
    @Column(name = "account", length = 64)
    private String account;

    /** 绑定类型：ANDROID_ALIYUN（列名对齐 token_type/level_type 惯例，规避跨库保留字） */
    @Column(name = "device_type", length = 32)
    private String type;

    /** 设备平台：ANDROID（预留 IOS） */
    @Column(name = "device", length = 32)
    private String device;

    /** 客户端渠道：FLUTTER 等 */
    @Column(name = "channel", length = 32)
    private String channel;
}
