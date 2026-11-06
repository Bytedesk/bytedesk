package com.bytedesk.ticket.process;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 默认工单流程初始化结果（内部/外部两条）。
 * outcome 语义：
 * - CREATED：记录不存在，本次按配对模板创建并部署
 * - REUSED：记录与有效部署均存在，本次复用，未覆盖内容
 * - REPAIRED：记录存在但 deploymentId/status 丢失，本次核实部署后修复回写
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessInitializationResult {

    private String orgUid;

    private List<Item> processes;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {

        private String uid;

        private String type;

        private String outcome;

        private String deploymentId;

        private String processDefinitionKey;
    }
}
