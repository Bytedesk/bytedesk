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

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 设备推送绑定对账仓储（由 PushTokenRepository 重命名迁入 push_device 包）
 *
 * <p>阿里云账号维度推送下，真实绑定在阿里云侧由移动端 bindAccount 完成，
 * 本表仅记录「该 userUid 已开通某供应商推送」用于候选池判定与推送漏斗对账。
 */
public interface PushDeviceRepository extends JpaRepository<PushDeviceEntity, Long>, JpaSpecificationExecutor<PushDeviceEntity> {

    Optional<PushDeviceEntity> findByUid(String uid);

    boolean existsByUid(String uid);

    Optional<PushDeviceEntity> findFirstByUserUidAndTypeAndDeletedFalse(String userUid, String type);

    List<PushDeviceEntity> findByUserUidAndDeletedFalse(String userUid);

    /** 同一 deviceId 换账号登录场景的对账查询 */
    Optional<PushDeviceEntity> findFirstByDeviceIdAndDeletedFalse(String deviceId);

    boolean existsByUserUidAndTypeAndDeletedFalse(String userUid, String type);
}
