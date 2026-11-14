package com.bytedesk.remote.session;

import java.time.ZonedDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.bytedesk.core.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "bytedesk_desktop_session", indexes = {
        @Index(name = "idx_remote_session_org", columnList = "org_uid,viewer_uid"),
        @Index(name = "idx_remote_session_expiry", columnList = "session_status,expires_at") })
public class DesktopSessionEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private String threadUid;
    @JsonIgnore
    @Column(unique = true)
    private String activeThreadUid;
    private String roomUid;
    private String viewerUid;
    private String viewerName;
    private String hostUid;
    private String hostDeviceUid;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "session_status", length = 32, nullable = false)
    private DesktopSessionStatusEnum status = DesktopSessionStatusEnum.INITIALIZED;
    @JsonIgnore
    @Column(length = 64)
    private String invitationHash;
    @JsonIgnore
    @Column(length = 64)
    private String hostTokenHash;
    private ZonedDateTime invitationExpiresAt;
    private ZonedDateTime expiresAt;
    private ZonedDateTime startedAt;
    private ZonedDateTime endedAt;
    private ZonedDateTime hostSeenAt;
    private ZonedDateTime viewerSeenAt;
    private boolean controlRequested;
    private boolean controlGranted;
    private long inputCount;
    @Column(length = 64)
    private String terminationReason;
    @JsonIgnore
    @JdbcTypeCode(SqlTypes.LONG32VARCHAR)
    private String hostSignals;
    @JsonIgnore
    @JdbcTypeCode(SqlTypes.LONG32VARCHAR)
    private String viewerSignals;
}
