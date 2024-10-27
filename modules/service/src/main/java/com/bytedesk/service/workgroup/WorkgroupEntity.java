/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-22 12:20:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.settings.ServiceSettings;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(value = { WorkgroupEntityListener.class })
@Table(name = "bytedesk_service_workgroup")
public class WorkgroupEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.DEFAULT_WORK_GROUP_AVATAR_URL;

    @Builder.Default
    private String description = I18Consts.I18N_WORKGROUP_DESCRIPTION;

    /**
     * route type
     */
    @Builder.Default
    // private String routeType = RouteConsts.ROUTE_TYPE_ROBIN;
    // private WorkgroupRouteEnum routeType = WorkgroupRouteEnum.ROBIN;
    private String routeType = WorkgroupRouteEnum.ROBIN.name();

    @Builder.Default
    private String status = WorkgroupStateEnum.AVAILABLE.name();

    /**
     * recent chat agent should be routed first
     */
    @Builder.Default
    @Column(name = "is_recent")
    private boolean recent = false;

    @Embedded
    @Builder.Default
    private ServiceSettings serviceSettings = new ServiceSettings();

    /**
     * one wg can have many agents, one agent can belong to many wgs
     */
    @JsonIgnore
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    // 为方便路由分配客服，特修改成list
    private List<AgentEntity> agents = new ArrayList<>();

    /**
     * 路由队列，用于分配客服
     */
    @Transient
    @Builder.Default
    private Queue<AgentEntity> agentQueue = new LinkedList<>();

    // TODO: 处理留言agent

    // TODO: 监控管理员agent

    /** 存储下一个待分配的客服等信息 */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // 用于兼容postgreSQL，否则会报错，[ERROR: column "extra" is of type json but expression is
    // of type character varying
    @JdbcTypeCode(SqlTypes.JSON)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    // TODO: 根据算法选择一个agent
    // TODO: 增加agent-currentThreadCount数量
    // TODO: 模拟测试10000个访客分配给10个客服，每个客服平均分配50个访客
    public AgentEntity nextAgent() {

        if (routeType.equals(WorkgroupRouteEnum.ROBIN)) {

            // return assignAgentByRobin();

        } else if (routeType.equals(WorkgroupRouteEnum.AVERAGE)) {

        } else if (routeType.equals(WorkgroupRouteEnum.IDLE)) {

        } else if (routeType.equals(WorkgroupRouteEnum.LESS)) {

        }

        return getAgents().iterator().next();
    }

    /**
     * 轮询分配算法实现访客到客服的分配
     * @return 分配到的客服
     */
    public AgentEntity assignAgentByRobin() {
        if (agentQueue.isEmpty()) {
            // 如果队列为空，则先将所有客服加入队列
            // agentQueue.addAll(agents);
            Iterator<AgentEntity> iterator = agents.iterator();
            while (iterator.hasNext()) {
                AgentEntity agent = iterator.next();
                if (agent.isConnected() && agent.isAvailable()) {
                    agentQueue.add(agent);
                }
            }
        }

        // 从队列头部获取一个客服
        AgentEntity assignedAgent = agentQueue.poll();

        // 为了保证轮询的连续性，将该客服重新加入队列尾部
        agentQueue.offer(assignedAgent);

        return assignedAgent;
    }

    public boolean isConnected() {
        if (this.agents == null || this.agents.isEmpty()) {
            return false;
        }
        return this.agents.stream().anyMatch(agent -> agent.isConnected());
    }
    
}
