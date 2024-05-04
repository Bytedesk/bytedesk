/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-13 16:14:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 10:20:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<Topic> findByUid(String uid);
    
    Optional<Topic> findFirstByUserUid(String userUid);

    // boolean existsByTopicAndUid(String topic, String uid);

    // exact match 精确匹配
    // List<Topic> findByTopicAndUid(String topic, String uid);

    /**
      * TODO: wildcard match 通配符匹配 topic
      * topic通配符说明：
      * 单层通配符"+"：只能匹配一层主题。例如，"aaa/+"可以匹配"aaa/bbb"，但不能匹配"aaa/bbb/ccc"。
      * 多层通配符"#"：可以匹配多层主题。例如，"aaa/#"不但可以匹配"aaa/bbb"，还可以匹配"aaa/bbb/ccc/ddd"。它必须作为主题的最后一个级别使用，并且只能出现在最后
      */
    // List<Topic> findByTopicStartsWith(String topic);
    // List<Topic> findByTopic(String topic);
    // 
    @Query(value="select * from core_topic where topics like %:topic% ", nativeQuery = true)
    Set<Topic> findByTopicsContains(@Param("topic") String topic);

    // void deleteByTopicAndUid(String topic, String uid);
}
