/*
 * @Author: jackning 270580156@qq.com
 *
 * 仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 */
package com.bytedesk.service.workgroup;

import java.util.List;

import lombok.Data;

@Data
public class WorkgroupAdminRequest {

    private String orgUid;

    private String agentUid;

    /**
     * 该 agent 作为管理员（监控/接管权限）的 workgroup uid 列表。
     * 后端将根据该列表对每个 workgroup 仅增删该 agent 一条关系，不影响其它管理员。
     */
    private List<String> workgroupUids;
}
