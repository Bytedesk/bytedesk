/*
 * @Author: jackning 270580156@qq.com
 *
 * 仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 */
package com.bytedesk.service.workgroup;

import java.util.List;

import lombok.Data;

@Data
public class WorkgroupAdminBatchRequest {

    private String orgUid;

    /**
     * 需要查询监控工作组的客服 uid 列表。
     */
    private List<String> agentUids;
}