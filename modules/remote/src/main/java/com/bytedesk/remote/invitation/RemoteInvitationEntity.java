package com.bytedesk.remote.invitation;

import java.time.ZonedDateTime;
import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "bytedesk_remote_invitation")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class RemoteInvitationEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @Column(length = 64, nullable = false)
    private String codeHash;
    @Column(length = 64, nullable = false)
    private String hostSecretHash;
    private ZonedDateTime expiresAt;
    private String sessionUid;
}
