package com.bytedesk.disk.archive;

import java.util.Map;
import java.util.UUID;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.disk.audit.DiskLogRepository;
import com.bytedesk.disk.audit.DiskLogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class DiskArchiveAudit {
    private final DiskLogRepository logs;

    @Transactional
    public void record(DiskArchiveAccess.Actor actor, String uid, String action, String result,
            Map<String, String> changes) {
        String requestId = UUID.randomUUID().toString();
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            String header = attributes.getRequest().getHeader("X-Request-ID");
            if (header != null && header.matches("[A-Za-z0-9._-]{1,64}")) requestId = header;
        }
        DiskLogEntity log = DiskLogEntity.builder().build();
        log.setUid(UUID.randomUUID().toString());
        log.setOrgUid(actor.orgUid());
        log.setUserUid(actor.userUid());
        log.setOperatorUid(actor.userUid());
        log.setNodeUid(uid);
        log.setAction("ARCHIVE_" + action);
        log.setResult(result);
        log.setDetail(JSON.toJSONString(Map.of("requestId", requestId, "changes", changes)));
        logs.saveAndFlush(log);
    }

    public void success(DiskArchiveAccess.Actor actor, String uid, String action) {
        record(actor, uid, action, "SUCCESS", Map.of());
    }
}
