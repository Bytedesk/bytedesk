/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:42:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-08 21:56:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PushRepository extends JpaRepository<PushEntity, Long>, JpaSpecificationExecutor<PushEntity> {

    Optional<PushEntity> findByUid(String uid);

    List<PushEntity> findByStatus(String status);

    Optional<PushEntity> findByStatusAndReceiverAndContent(String status, String receiver, String content);

    // 注意：历史上同一 deviceUid 可能存在多条记录，这里提供按更新时间倒序取最新一条，避免非唯一结果异常
    Optional<PushEntity> findTopByDeviceUidOrderByUpdatedAtDesc(String deviceUid);

    Optional<PushEntity> findByDeviceUidAndContent(String deviceUid, String code);

    Boolean existsByStatusAndTypeAndReceiver(String status, String type, String receiver);

    Boolean existsByStatusAndTypeAndReceiverAndContent(String status, String type, String receiver,
            String content);
}
