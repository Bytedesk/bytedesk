package com.bytedesk.call.queue;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * FreeSwitch队列数据访问接口
 */
@Repository
public interface FreeSwitchQueueRepository extends JpaRepository<FreeSwitchQueueEntity, Long> {
    
    /**
     * 根据队列名称查找队列
     */
    Optional<FreeSwitchQueueEntity> findByName(String name);
    
    /**
     * 根据队列类型查找队列列表
     */
    List<FreeSwitchQueueEntity> findByType(FreeSwitchQueueEntity.QueueType type);
    
    /**
     * 根据队列状态查找队列列表
     */
    List<FreeSwitchQueueEntity> findByStatus(FreeSwitchQueueEntity.QueueStatus status);
    
    /**
     * 检查队列名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据队列名称删除队列
     */
    void deleteByName(String name);
    
    /**
     * 根据队列类型和状态查找队列
     */
    List<FreeSwitchQueueEntity> findByTypeAndStatus(
        FreeSwitchQueueEntity.QueueType type,
        FreeSwitchQueueEntity.QueueStatus status);
} 