package com.bytedesk.ticket.ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * G2 组合可见性查询上下文（无 type 混合列表查询时按工单类型分别应用）。
 *
 * <p>desktop 等客服工作台的工单列表默认不传 {@code type}（同时返回内部+外部工单），
 * 旧实现笼统按 EXTERNAL 设置过滤导致 INTERNAL 工单漏过/误伤（「全部 45 ≠ 内部 20 + 外部 15」）。
 * 组合谓词下「全部工单」列表 = 「内部工单」列表 + 「外部工单」列表口径一致。
 *
 * <p>该对象仅为查询期上下文载体，不落库。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketVisibilityQueryContext {

    /** ORG_WIDE / DEPARTMENT_RESTRICTED / DEPARTMENT_BASED / CATEGORY_BASED */
    private String mode;

    /** mode != ORG_WIDE 时为 true */
    @Builder.Default
    private boolean restricted = false;

    /** DEPARTMENT_BASED 下允许查看该设置对应工单的部门 uid 列表 */
    @Builder.Default
    private List<String> allowedDepartmentUids = new ArrayList<>();

    /** CATEGORY_BASED 下 visibility=DEPARTMENT_RESTRICTED 的规则分类 */
    @Builder.Default
    private List<String> restrictedCategoryUids = new ArrayList<>();

    /** CATEGORY_BASED 下 visibility=DEPARTMENT_BASED 的分类 → 允许部门 uid 列表 */
    @Builder.Default
    private Map<String, List<String>> restrictedCategoryDepartmentUids = new HashMap<>();
}
