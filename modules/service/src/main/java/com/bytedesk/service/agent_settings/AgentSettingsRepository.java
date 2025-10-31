/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Agent settings repository
 */
package com.bytedesk.service.agent_settings;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

/**
 * Repository for AgentSettingsEntity
 */
@Repository
public interface AgentSettingsRepository extends JpaRepository<AgentSettingsEntity, Long>, JpaSpecificationExecutor<AgentSettingsEntity> {

    Optional<AgentSettingsEntity> findByUid(String uid);
    
    /**
     * 查找 AgentSettings 并使用 JOIN FETCH 加载第一级关联
     * 加载 serviceSettings 和 draftServiceSettings,但不加载它们的集合
     * 
     * Note: 由于 Hibernate MultipleBagFetchException 限制,
     * 不能在一个查询中同时 JOIN FETCH 多个 List 集合
     * FAQ 集合将在事务内通过访问触发懒加载
     */
    @Query("select a from AgentSettingsEntity a " +
           "left join fetch a.serviceSettings " +
           "left join fetch a.draftServiceSettings " +
           "where a.uid = :uid")
    Optional<AgentSettingsEntity> findByUidWithCollections(@Param("uid") String uid);

    List<AgentSettingsEntity> findByOrgUid(String orgUid);

    Optional<AgentSettingsEntity> findByOrgUidAndIsDefaultTrue(String orgUid);

    /**
     * 使用悲观锁读取默认配置，防止并发下重复创建/更新导致的冲突
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AgentSettingsEntity a where a.orgUid = :orgUid and a.isDefault = true")
    Optional<AgentSettingsEntity> findDefaultForUpdate(@Param("orgUid") String orgUid);

    List<AgentSettingsEntity> findByOrgUidAndEnabledTrue(String orgUid);

    Boolean existsByNameAndOrgUid(String name, String orgUid);
}
