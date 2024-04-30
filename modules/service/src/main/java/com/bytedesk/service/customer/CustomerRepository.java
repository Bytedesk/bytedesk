/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:06:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-12 15:12:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.customer;

import java.util.Collection;

import org.springframework.data.repository.Repository;

/**
 * https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html
 */
public interface CustomerRepository extends Repository<Customer, Long>{

    Collection<CustomerNameOnly> findByName(String name);
    // using a dynamic projection parameter
    <T> Collection<T> findByName(String name, Class<T> type);
}
