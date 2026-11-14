package com.bytedesk.remote.audit;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "bytedesk_desktop_audit_log", indexes = {
        @Index(name = "idx_remote_audit_session", columnList = "session_uid,created_at") })
public class DesktopAuditLogEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private String sessionUid;
    @Column(length = 64)
    private String eventType;
    @Column(length = 255)
    private String details;
}
