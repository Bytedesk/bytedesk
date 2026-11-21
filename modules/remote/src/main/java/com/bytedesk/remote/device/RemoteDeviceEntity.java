package com.bytedesk.remote.device;

import java.time.ZonedDateTime;
import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "bytedesk_remote_device", indexes = {
        @Index(name = "idx_remote_device_org", columnList = "org_uid") })
public class RemoteDeviceEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @Column(length = 128)
    private String deviceName;
    @Column(length = 32)
    private String osType;
    private boolean online;
    private ZonedDateTime lastOnline;
}
