package com.bytedesk.service.queue.dto;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.service.queue_member.QueueMemberResponse;

import lombok.Builder;
import lombok.Value;

/**
 * Snapshot wrapper returned by GET /api/v1/queue/agent/{agentUid}/members.
 */
@Value
@Builder
public class AgentQueueSnapshotResponse {

    @Builder.Default
    List<QueueMemberResponse> members = Collections.emptyList();

    long totalElements;

    int page;

    int size;

    @Builder.Default
    ZonedDateTime lastUpdatedAt = BdDateUtils.now();

    public static AgentQueueSnapshotResponse empty(Pageable pageable) {
        return AgentQueueSnapshotResponse.builder()
                .members(Collections.emptyList())
                .totalElements(0)
                .page(pageable != null ? pageable.getPageNumber() : 0)
                .size(pageable != null ? pageable.getPageSize() : 0)
                .lastUpdatedAt(BdDateUtils.now())
                .build();
    }

    public static AgentQueueSnapshotResponse from(Page<QueueMemberResponse> page) {
        if (page == null) {
            return empty(Pageable.unpaged());
        }
        return AgentQueueSnapshotResponse.builder()
                .members(page.getContent())
                .totalElements(page.getTotalElements())
                .page(page.getNumber())
                .size(page.getSize())
                .lastUpdatedAt(BdDateUtils.now())
                .build();
    }
}
