package com.bytedesk.disk.archive;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class DiskArchiveResponse {
    private String uid;
    private int version;
    private String module;
    private String originalName;
    private String contentType;
    private long size;
    private String contractNumber;
    private String counterparty;
    private String invoiceNumber;
    private String amount;
    private String currency;
    private LocalDate signingDate;
    private LocalDate invoiceDate;
    private String remark;
    private boolean deleted;
    private ZonedDateTime updatedAt;
    private ZonedDateTime createdAt;
    private String uploadState;
    private java.util.List<String> actions = java.util.List.of();
}
