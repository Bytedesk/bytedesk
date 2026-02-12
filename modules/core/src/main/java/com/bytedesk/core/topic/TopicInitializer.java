/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-06 21:43:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-26 10:52:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;
import com.bytedesk.core.topic_subscription.TopicSubscriptionEntity;
import com.bytedesk.core.topic_subscription.TopicSubscriptionRepository;
import com.bytedesk.core.uid.UidUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopicInitializer implements SmartInitializingSingleton {

    private final AuthorityRestService authorityRestService;

    private final TopicRepository topicRepository;

    private final TopicSubscriptionRepository topicSubscriptionRepository;

    private final UidUtils uidUtils;

    @Value("${bytedesk.topic.migrate-subscriptions-on-startup:true}")
    private boolean migrateSubscriptionsOnStartup;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        if (migrateSubscriptionsOnStartup) {
            migrateTopicEntityTopicsToSubscriptions();
        }
        // create default
        // String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        // topicRestService.initTopics(orgUid);
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = TopicPermissions.TOPIC_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }

    /**
     * Backfill subscription truth table from legacy TopicEntity.topics.
     *
     * Idempotent: if (userUid, topic) already exists, it will be kept; if exists but deleted=true, it will be re-activated.
     */
    @Transactional
    public void migrateTopicEntityTopicsToSubscriptions() {
        final int pageSize = 500;
        int pageNumber = 0;
        long scannedUsers = 0;
        long scannedTopics = 0;
        long inserted = 0;
        long reactivated = 0;
        long skippedInvalid = 0;

        while (true) {
            Page<TopicEntity> page = topicRepository.findAll(PageRequest.of(pageNumber, pageSize));
            if (!page.hasContent()) {
                break;
            }

            for (TopicEntity topicEntity : page.getContent()) {
                if (topicEntity == null || topicEntity.isDeleted()) {
                    continue;
                }
                String userUid = topicEntity.getUserUid();
                if (!StringUtils.hasText(userUid)) {
                    skippedInvalid++;
                    continue;
                }
                scannedUsers++;
                if (topicEntity.getTopics() == null || topicEntity.getTopics().isEmpty()) {
                    continue;
                }
                for (String topic : topicEntity.getTopics()) {
                    scannedTopics++;
                    if (!StringUtils.hasText(topic)) {
                        skippedInvalid++;
                        continue;
                    }

                    var optional = topicSubscriptionRepository.findFirstByUserUidAndTopic(userUid, topic);
                    if (optional.isPresent()) {
                        TopicSubscriptionEntity existing = optional.get();
                        if (existing.isDeleted()) {
                            existing.setDeleted(false);
                            topicSubscriptionRepository.save(existing);
                            reactivated++;
                        }
                        continue;
                    }

                    TopicSubscriptionEntity subscription = TopicSubscriptionEntity.builder()
                            .uid(uidUtils.getUid())
                            .userUid(userUid)
                            .topic(topic)
                            .build();
                    topicSubscriptionRepository.save(subscription);
                    inserted++;
                }
            }

            if (!page.hasNext()) {
                break;
            }
            pageNumber++;
        }

        log.info(
                "topic migrateTopicEntityTopicsToSubscriptions done scannedUsers={} scannedTopics={} inserted={} reactivated={} skippedInvalid={} pageSize={}",
                scannedUsers, scannedTopics, inserted, reactivated, skippedInvalid, pageSize);
    }

    

}
