package com.bytedesk.ticket.ticket.assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.redis.RedisRoundRobinSequenceService;
import com.bytedesk.ticket.ticket.TicketEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 工单成员轮询服务：按实际候选来源隔离作用域，通过 Redis 原子取号实现严格轮询。
 *
 * <p>替代旧的“查询上一条已分配工单推导游标”算法。旧算法的三类缺陷：
 * <ol>
 *   <li>无部门表示不一致（NULL vs 空字符串）导致历史查询永远取不到/取错游标</li>
 *   <li>历史查询按 createdAt 排序、未按类型/实际候选范围隔离，且含显式指派/人工改派记录</li>
 *   <li>选人读取历史与分配写入之间存在并发窗口，并发事务读到同一游标</li>
 * </ol>
 *
 * <p>作用域规则（orgUid + ticketType + scopeKind + scopeId）：
 * <ul>
 *   <li>WORKGROUP / workgroupUid —— 工作组在线客服池（由 service 侧选人，不在此取号）</li>
 *   <li>DEPARTMENT / departmentUid —— 单个有效部门（表单部门或节点明确指定）</li>
 *   <li>ORGANIZATION / orgUid —— 未选部门（NULL、空字符串、纯空白统一归一化）的组织成员兜底</li>
 *   <li>NODE_DEPARTMENTS / 流程UID+节点key+排序去重部门集合 —— 节点明确指定的多个部门</li>
 *   <li>NODE_ROLE / 流程UID+节点key+roleUid —— 节点角色候选</li>
 * </ul>
 *
 * <p>候选资格：同组织、未删除、非空 UID 的 Member（不要求在线或具备 Agent 身份）；
 * 按 UID 去重并稳定排序（TreeMap），保证轮询顺序与数据库返回顺序无关。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketRoundRobinScopeService {

    private final RedisRoundRobinSequenceService roundRobinSequenceService;

    /** 节点多部门/角色作用域 ID 的段分隔符（配合长度编码避免歧义） */
    static final String NODE_SCOPE_SEPARATOR = "|";

    /**
     * 按部门作用域轮询选人。
     *
     * @return 选中的成员；候选为空返回 null（不取号）
     */
    public MemberEntity roundRobinDepartment(TicketEntity ticket, String departmentUid, List<MemberEntity> candidates) {
        return roundRobin(ticket, ScopeKind.DEPARTMENT, departmentUid, candidates);
    }

    /**
     * 按组织兜底作用域轮询选人（工单未选部门：NULL/空字符串/纯空白统一归一化到 ORGANIZATION 作用域）。
     */
    public MemberEntity roundRobinOrganization(TicketEntity ticket, List<MemberEntity> candidates) {
        return roundRobin(ticket, ScopeKind.ORGANIZATION, ticket.getOrgUid(), candidates);
    }

    /**
     * 按节点多部门作用域轮询选人。
     *
     * @param processEntityUid 流程 UID
     * @param taskDefinitionKey 节点 key
     * @param departmentUids 排序去重后的部门 UID 列表
     */
    public MemberEntity roundRobinNodeDepartments(TicketEntity ticket, String processEntityUid,
            String taskDefinitionKey, List<String> departmentUids, List<MemberEntity> candidates) {
        String scopeId = buildNodeScopeId(processEntityUid, taskDefinitionKey, String.join(NODE_SCOPE_SEPARATOR, departmentUids));
        return roundRobin(ticket, ScopeKind.NODE_DEPARTMENTS, scopeId, candidates);
    }

    /**
     * 按节点角色作用域轮询选人。
     */
    public MemberEntity roundRobinNodeRole(TicketEntity ticket, String processEntityUid,
            String taskDefinitionKey, String roleUid, List<MemberEntity> candidates) {
        String scopeId = buildNodeScopeId(processEntityUid, taskDefinitionKey, roleUid);
        return roundRobin(ticket, ScopeKind.NODE_ROLE, scopeId, candidates);
    }

    private MemberEntity roundRobin(TicketEntity ticket, ScopeKind scopeKind, String scopeId,
            List<MemberEntity> candidates) {
        List<MemberEntity> normalized = normalizeCandidates(ticket, candidates);
        if (normalized.isEmpty()) {
            return null;
        }
        String scopeKey = buildScopeKey(ticket, scopeKind, scopeId);
        long sequence = roundRobinSequenceService.nextSequence(scopeKey);
        int index = (int) Math.floorMod(sequence - 1L, (long) normalized.size());
        MemberEntity selected = normalized.get(index);
        if (log.isDebugEnabled()) {
            log.debug("ticket round robin: ticket={}, scope={}/{}, sequence={}, candidateCount={}, selected={}",
                    ticket.getUid(), scopeKind, scopeId, sequence, normalized.size(), selected.getUid());
        }
        return selected;
    }

    /**
     * 候选归一化：校验同组织、未删除、非空 UID；按 UID 去重并稳定排序。
     * 数据库返回顺序、重复行不影响轮询顺序。
     */
    List<MemberEntity> normalizeCandidates(TicketEntity ticket, List<MemberEntity> candidates) {
        Map<String, MemberEntity> unique = new TreeMap<>();
        if (candidates != null) {
            String orgUid = ticket != null ? ticket.getOrgUid() : null;
            for (MemberEntity member : candidates) {
                if (member == null || !StringUtils.hasText(member.getUid())) {
                    continue;
                }
                if (member.isDeleted()) {
                    continue;
                }
                if (StringUtils.hasText(orgUid) && StringUtils.hasText(member.getOrgUid())
                        && !orgUid.equals(member.getOrgUid())) {
                    continue;
                }
                unique.putIfAbsent(member.getUid(), member);
            }
        }
        return new ArrayList<>(unique.values());
    }

    private String buildScopeKey(TicketEntity ticket, ScopeKind scopeKind, String scopeId) {
        String orgUid = ticket != null ? ticket.getOrgUid() : "";
        String ticketType = ticket != null && StringUtils.hasText(ticket.getType()) ? ticket.getType() : "";
        return enc(orgUid) + ":" + enc(ticketType) + ":" + enc(scopeKind.name()) + ":" + enc(scopeId);
    }

    static String buildNodeScopeId(String processEntityUid, String taskDefinitionKey, String rawId) {
        return (processEntityUid == null ? "" : processEntityUid.trim())
                + NODE_SCOPE_SEPARATOR + (taskDefinitionKey == null ? "" : taskDefinitionKey.trim())
                + NODE_SCOPE_SEPARATOR + (rawId == null ? "" : rawId.trim());
    }

    private static String enc(String segment) {
        String value = segment == null ? "" : segment.trim();
        return value.length() + "_" + value;
    }

    enum ScopeKind {
        WORKGROUP,
        DEPARTMENT,
        ORGANIZATION,
        NODE_DEPARTMENTS,
        NODE_ROLE
    }
}
