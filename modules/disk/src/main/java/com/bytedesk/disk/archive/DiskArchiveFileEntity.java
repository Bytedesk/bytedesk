package com.bytedesk.disk.archive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "bytedesk_disk_archive_file", indexes = {
        @Index(name = "idx_daf_module", columnList = "org_uid,module,is_deleted"),
        @Index(name = "idx_daf_recent", columnList = "org_uid,module,created_at"),
        @Index(name = "uk_daf_request", columnList = "request_key", unique = true)
})
public class DiskArchiveFileEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @Column(nullable = false, length = 16)
    private String module;
    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;
    @Column(nullable = false, length = 128)
    private String contentType;
    @Column(name = "file_size", nullable = false)
    private long size;
    @Column(nullable = false, length = 32)
    private String storageBackend;
    @Column(nullable = false, length = 64)
    private String storageKey;
    @Column(name = "upload_state", nullable = false, length = 16)
    private String uploadState;
    @Column(name = "upload_request_id", nullable = false, length = 64)
    private String uploadRequestId;
    @Column(name = "request_key", nullable = false, length = 64)
    private String requestKey;
    @Column(name = "contract_number", length = 200)
    private String contractNumber;
    @Column(length = 255)
    private String counterparty;
    @Column(name = "invoice_number", length = 200)
    private String invoiceNumber;
    @Column(precision = 19, scale = 2)
    private BigDecimal amount;
    @Column(length = 3)
    private String currency;
    @Column(name = "signing_date")
    private LocalDate signingDate;
    @Column(name = "invoice_date")
    private LocalDate invoiceDate;
    @Column(length = 2000)
    private String remark;
    @Column(name = "storage_cleared", nullable = false)
    private boolean storageCleared;
    private ZonedDateTime deletedAt;
    private String deletedBy;
}
