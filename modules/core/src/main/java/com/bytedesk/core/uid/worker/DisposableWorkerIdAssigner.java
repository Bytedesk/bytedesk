/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2021-02-24 15:52:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 16:11:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.uid.worker;

import com.bytedesk.core.uid.UidGeneratorEntity;
import com.bytedesk.core.uid.UidGereratorRepository;
// import com.bytedesk.core.uid.utils.DockerUtils;
import com.bytedesk.core.uid.utils.NetUtils;
import com.bytedesk.core.utils.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
// import org.apache.commons.lang.math.RandomUtils;
// import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * Represents an implementation of {@link WorkerIdAssigner},
 * the worker id will be discarded after assigned to the UidGenerator
 *
 * @author yutianbao
 */
public class DisposableWorkerIdAssigner implements WorkerIdAssigner {

    // @Value("${server.host}")
    // private String host;

    @Value("${server.port}")
    private String port;

    // @Resource
    @Autowired
    private UidGereratorRepository workerNodeDAO;

    /**
     * Assign worker id base on database.<p&gt;
     * If there is host name and port in the environment, we considered that the node runs in Docker container<br&gt;
     * Otherwise, the node runs on an actual machine.
     *
     * @return assigned worker id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public long assignWorkerId() {
        // build worker node entity
        UidGeneratorEntity workerNodeEntity = buildWorkerNode();

        UidGeneratorEntity oldWorkerNode = workerNodeDAO
                .findByHostAndPort(workerNodeEntity.getHost(), workerNodeEntity.getPort());
        if (null != oldWorkerNode) {
            return oldWorkerNode.getId();
        }

        // add worker node for new (ignore the same IP + PORT)
        workerNodeDAO.save(workerNodeEntity);
        // LOGGER.info("Add worker node:" + workerNodeEntity);

        return workerNodeEntity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long assignFakeWorkerId() {
        return buildFakeWorkerNode().getId();
    }

    /**
     * Build worker node entity by IP and PORT
     */
    private UidGeneratorEntity buildWorkerNode() {
        UidGeneratorEntity workerNodeEntity = new UidGeneratorEntity();
        workerNodeEntity.setUid(Utils.getUid());
    
        workerNodeEntity.setType(WorkerNodeType.ACTUAL.value());
        workerNodeEntity.setHost(NetUtils.getLocalAddress());
        // workerNodeEntity.setPort(System.currentTimeMillis() + "-" + new Random().nextInt(100000));
        // workerNodeEntity.setHost(host);
        workerNodeEntity.setPort(port); 
    
        return workerNodeEntity;
    }

    private UidGeneratorEntity buildFakeWorkerNode() {
        UidGeneratorEntity workerNodeEntity = new UidGeneratorEntity();
        workerNodeEntity.setUid(Utils.getUid());

        workerNodeEntity.setType(WorkerNodeType.FAKE.value());
        workerNodeEntity.setHost(NetUtils.getLocalAddress());
        // workerNodeEntity.setPort(System.currentTimeMillis() + "-" + new Random().nextInt(100000));
        // workerNodeEntity.setHost(host);
        workerNodeEntity.setPort(port);
    
        return workerNodeEntity;
    }
}
