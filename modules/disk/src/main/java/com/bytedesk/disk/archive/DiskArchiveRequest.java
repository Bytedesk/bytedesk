package com.bytedesk.disk.archive;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class DiskArchiveRequest {
    private String uid;
    private Integer version;
    private String module;
    private String uploadRequestId;
    private String originalName;
    private String contractNumber;
    private String counterparty;
    private String invoiceNumber;
    private BigDecimal amount;
    private String currency;
    private LocalDate signingDate;
    private LocalDate invoiceDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String remark;
    private String search;
    private int pageNumber;
    private int pageSize = 20;
}
