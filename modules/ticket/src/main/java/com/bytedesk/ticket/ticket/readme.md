<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-11 16:14:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-11 16:38:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
-->
# ticket

```java
public static Specification<TicketEntity> search(TicketRequest request) {
    return (root, query, criteriaBuilder) -> {
        query.distinct(true);
        
        List<Predicate> predicates = new ArrayList<>();
        predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
        
        if (StringUtils.hasText(request.getTitle())) {
            predicates.add(criteriaBuilder.like(root.get("title"), "%" + request.getTitle() + "%"));
        }
        if (StringUtils.hasText(request.getDescription())) {
            predicates.add(criteriaBuilder.like(root.get("description"), "%" + request.getDescription() + "%"));
        }
        if (StringUtils.hasText(request.getSearchText())) {
            predicates.add(criteriaBuilder.or(
                criteriaBuilder.like(root.get("uid"), "%" + request.getSearchText() + "%"),
                criteriaBuilder.like(root.get("title"), "%" + request.getSearchText() + "%"),
                criteriaBuilder.like(root.get("description"), "%" + request.getSearchText() + "%")
            ));
        }
        if (StringUtils.hasText(request.getStatus())) {
            predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
        }
        if (StringUtils.hasText(request.getPriority())) {
            predicates.add(criteriaBuilder.equal(root.get("priority"), request.getPriority()));
        }
        if (StringUtils.hasText(request.getCategoryUid())) {
            predicates.add(criteriaBuilder.equal(root.get("category").get("uid"), request.getCategoryUid()));
        }
        if (StringUtils.hasText(request.getThreadTopic())) {
            predicates.add(criteriaBuilder.equal(root.get("thread").get("topic"), request.getThreadTopic()));
        }
        if (StringUtils.hasText(request.getWorkgroupUid())) {
            predicates.add(criteriaBuilder.equal(root.get("workgroup").get("uid"), request.getWorkgroupUid()));
        }
        // 处理 ALL 查询 - 我创建的或待我处理的
        if (StringUtils.hasText(request.getReporterUid()) || StringUtils.hasText(request.getAssigneeUid())) {
            if (Boolean.TRUE.equals(request.getAssignmentAll())) {
                List<Predicate> orPredicates = new ArrayList<>();
                
                // 处理 reporter 条件
                if (StringUtils.hasText(request.getReporterUid())) {
                    var reporterJoin = root.join("reporter", JoinType.LEFT);
                    orPredicates.add(criteriaBuilder.equal(reporterJoin.get("uid"), request.getReporterUid()));
                }
                
                // 处理 assignee 条件
                if (StringUtils.hasText(request.getAssigneeUid())) {
                    var assigneeJoin = root.join("assignee", JoinType.LEFT);
                    orPredicates.add(criteriaBuilder.equal(assigneeJoin.get("uid"), request.getAssigneeUid()));
                }
                
                // 组合 OR 条件
                if (!orPredicates.isEmpty()) {
                    predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
                }
            } else {

                if (TicketConsts.TICKET_FILTER_UNASSIGNED.equals(request.getAssigneeUid())) {
                    predicates.add(criteriaBuilder.isNull(root.get("assignee")));
                } else {
                    // 单一条件查询
                    if (StringUtils.hasText(request.getReporterUid())) {
                        var reporterJoin = root.join("reporter");
                        predicates.add(criteriaBuilder.equal(reporterJoin.get("uid"), request.getReporterUid()));
                    }
                    if (StringUtils.hasText(request.getAssigneeUid())) {
                        var assigneeJoin = root.join("assignee");
                        predicates.add(criteriaBuilder.equal(assigneeJoin.get("uid"), request.getAssigneeUid()));
                    }
                }
            }
        }
        // 处理日期范围查询
        if (StringUtils.hasText(request.getStartDate())) {
            LocalDateTime startDate = LocalDateTime.parse(request.getStartDate(), formatter);
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
        }
        if (StringUtils.hasText(request.getEndDate())) {
            LocalDateTime endDate = LocalDateTime.parse(request.getEndDate(), formatter);
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
        }

        // 添加排序
        query.orderBy(criteriaBuilder.desc(root.get("updatedAt")));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
}
```

```java
// 字段为 json 格式
if (StringUtils.hasText(request.getWorkgroupUid())) {
    predicates.add(criteriaBuilder.equal(
        criteriaBuilder.function(
            "JSON_CONTAINS", 
            Integer.class,
            root.get("workgroup"),
            criteriaBuilder.literal("{\"uid\":\"" + request.getWorkgroupUid() + "\"}")
        ),
        1
    ));
}
// 处理 ALL 查询 - 我创建的或待我处理的
if (StringUtils.hasText(request.getReporterUid()) || StringUtils.hasText(request.getAssigneeUid())) {
    if (Boolean.TRUE.equals(request.getAssignmentAll())) {
        List<Predicate> orPredicates = new ArrayList<>();
        
        // 处理 reporter 条件
        if (StringUtils.hasText(request.getReporterUid())) {
            orPredicates.add(criteriaBuilder.equal(
                criteriaBuilder.function(
                    "JSON_CONTAINS", 
                    Integer.class,
                    root.get("reporter"),
                    criteriaBuilder.literal("{\"uid\":\"" + request.getReporterUid() + "\"}")
                ),
                1
            ));
        }
        
        // 处理 assignee 条件
        if (StringUtils.hasText(request.getAssigneeUid())) {
            if (TicketConsts.TICKET_FILTER_UNASSIGNED.equals(request.getAssigneeUid())) {
                orPredicates.add(criteriaBuilder.or(
                    criteriaBuilder.isNull(root.get("assignee")),
                    criteriaBuilder.equal(root.get("assignee"), BytedeskConsts.EMPTY_JSON_STRING)
                ));
            } else {
                orPredicates.add(criteriaBuilder.equal(
                    criteriaBuilder.function(
                        "JSON_CONTAINS", 
                        Integer.class,
                        root.get("assignee"),
                        criteriaBuilder.literal("{\"uid\":\"" + request.getAssigneeUid() + "\"}")
                    ),
                    1
                ));
            }
        }
        
        // 组合 OR 条件
        if (!orPredicates.isEmpty()) {
            predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
        }
    } else {
        // 单一条件查询
        if (StringUtils.hasText(request.getReporterUid())) {
            predicates.add(criteriaBuilder.equal(
                criteriaBuilder.function(
                    "JSON_CONTAINS", 
                    Integer.class,
                    root.get("reporter"),
                    criteriaBuilder.literal("{\"uid\":\"" + request.getReporterUid() + "\"}")
                ),
                1
            ));
        }
        if (StringUtils.hasText(request.getAssigneeUid())) {
            if (TicketConsts.TICKET_FILTER_UNASSIGNED.equals(request.getAssigneeUid())) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.isNull(root.get("assignee")),
                    criteriaBuilder.equal(root.get("assignee"), BytedeskConsts.EMPTY_JSON_STRING)
                ));
            } else {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.function(
                        "JSON_CONTAINS", 
                        Integer.class,
                        root.get("assignee"),
                        criteriaBuilder.literal("{\"uid\":\"" + request.getAssigneeUid() + "\"}")
                    ),
                    1
                ));
            }
        }
    }
}
```
