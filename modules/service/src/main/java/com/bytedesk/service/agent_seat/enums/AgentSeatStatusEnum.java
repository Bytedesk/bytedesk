package com.bytedesk.service.agent_seat.enums;

public enum AgentSeatStatusEnum {
    /** 可用 — 未绑定客服，可分配 */
    AVAILABLE,
    /** 已占用 — 已绑定客服，使用中 */
    OCCUPIED,
    /** 已过期 — 到期时间已过 */
    EXPIRED,
    /** 已回收 — 已被策略回收删除 */
    RECYCLED
}