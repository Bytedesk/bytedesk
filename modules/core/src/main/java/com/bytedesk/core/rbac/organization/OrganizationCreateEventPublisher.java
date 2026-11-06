package com.bytedesk.core.rbac.organization;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.config.BytedeskEventPublisher;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 组织创建事件的提交后发布器。
 *
 * afterCommit() 回调执行时，原事务已提交但 Spring 事务资源（连接绑定）可能仍挂起在线程上，
 * 此时同步监听器内默认 REQUIRED 的写事务会「参与已提交事务」而无法再次提交，
 * 表现为日志显示 create/save 成功但数据在新连接上不可见。
 *
 * 通过 Spring 代理调用 NOT_SUPPORTED 方法先挂起残留事务资源，再重查组织并发布事件，
 * 让各监听器自身的 @Transactional 能正常开启可提交的新事务。
 */
@Slf4j
@Component
@AllArgsConstructor
public class OrganizationCreateEventPublisher {

    private final OrganizationRepository organizationRepository;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    /**
     * 必须经由 Spring 代理调用（NOT_SUPPORTED 才生效）；发布顺序与监听器顺序保持现状。
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void publishAfterCommit(String orgUid) {
        Optional<OrganizationEntity> orgOptional = organizationRepository.findByUid(orgUid);
        if (orgOptional.isEmpty()) {
            log.warn("organization {} not found after commit, skip OrganizationCreateEvent", orgUid);
            return;
        }
        bytedeskEventPublisher.publishOrganizationCreateEvent(orgOptional.get());
    }
}
