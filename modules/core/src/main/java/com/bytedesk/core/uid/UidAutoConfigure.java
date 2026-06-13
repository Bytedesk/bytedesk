/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2021-02-24 15:52:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-07 15:11:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.uid;

import com.bytedesk.core.uid.impl.CachedUidGenerator;
import com.bytedesk.core.uid.impl.UidProperties;
import com.bytedesk.core.uid.buffer.RejectedPutBufferHandler;
import com.bytedesk.core.uid.buffer.RejectedTakeBufferHandler;
import com.bytedesk.core.uid.worker.DisposableWorkerIdAssigner;
import com.bytedesk.core.uid.worker.WorkerIdAssigner;
import com.bytedesk.core.uid.impl.DefaultUidGenerator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import lombok.RequiredArgsConstructor;

/**
 * https://github.com/wujun234/uid-generator-spring-boot-starter
 * UID 的自动配置
 *
 * @author wujun
 * @date 2019.02.20 10:57
 */
@RequiredArgsConstructor
@Configuration
@ConditionalOnClass({ DefaultUidGenerator.class, CachedUidGenerator.class })
@EnableConfigurationProperties(UidProperties.class)
public class UidAutoConfigure {

	private final UidProperties uidProperties;

	@Bean
	@ConditionalOnMissingBean
	@Lazy
	DefaultUidGenerator defaultUidGenerator(WorkerIdAssigner workerIdAssigner) {
		return new DefaultUidGenerator(uidProperties, workerIdAssigner);
	}

	@Bean
	@ConditionalOnMissingBean
	@Lazy
	CachedUidGenerator cachedUidGenerator(WorkerIdAssigner workerIdAssigner,
			ObjectProvider<RejectedPutBufferHandler> rejectedPutBufferHandlerProvider,
			ObjectProvider<RejectedTakeBufferHandler> rejectedTakeBufferHandlerProvider) {
		return new CachedUidGenerator(uidProperties,
				workerIdAssigner,
				rejectedPutBufferHandlerProvider,
				rejectedTakeBufferHandlerProvider);
	}

	@Bean
	@ConditionalOnMissingBean
    WorkerIdAssigner workerIdAssigner(UidGereratorRepository uidGereratorRepository) {
		return new DisposableWorkerIdAssigner(uidGereratorRepository);
	}
}
