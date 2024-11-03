/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-30 15:59:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-28 10:49:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public abstract class BaseSpecification {

    

    public static List<Predicate> getBasicPredicates(Root<?> root, CriteriaBuilder criteriaBuilder, String orgUid) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("orgUid"), orgUid));
        predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
        return predicates;
    }

}
