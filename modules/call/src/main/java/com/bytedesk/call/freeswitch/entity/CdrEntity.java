package com.bytedesk.call.freeswitch.entity;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import lombok.Data;

// import java.sql.Timestamp;

// /**
//  * 映射 FreeSWITCH 数据库中的 cdr 表（用于对账/迁移/旁路读取）。
//  * 注意：该实体不参与本地 CDR 写入，仅用于可选的读取场景。
//  */
// @Data
// @Entity
// @Table(name = "cdr")
// public class CdrEntity {
//     @Column(name = "caller_id_name")
//     private String callerIdName;
//     @Column(name = "caller_id_number")
//     private String callerIdNumber;
//     @Column(name = "destination_number")
//     private String destinationNumber;
//     @Column(name = "context")
//     private String context;
//     @Column(name = "start_stamp")
//     private Timestamp startStamp;
//     @Column(name = "answer_stamp")
//     private Timestamp answerStamp;
//     @Column(name = "end_stamp")
//     private Timestamp endStamp;
//     @Column(name = "duration")
//     private Integer duration;
//     @Column(name = "billsec")
//     private Integer billsec;
//     @Column(name = "hangup_cause")
//     private String hangupCause;

//     @Id
//     @Column(name = "uuid")
//     private String uuid;

//     @Column(name = "bleg_uuid")
//     private String blegUuid;
//     @Column(name = "account_code")
//     private String accountCode;
//     @Column(name = "domain_name")
//     private String domainName;
//     @Column(name = "read_codec")
//     private String readCodec;
//     @Column(name = "write_codec")
//     private String writeCodec;
//     @Column(name = "sip_hangup_disposition")
//     private String sipHangupDisposition;
//     @Column(name = "ani")
//     private String ani;
//     @Column(name = "aniii")
//     private String aniii;
//     @Column(name = "network_addr")
//     private String networkAddr;
//     @Column(name = "json_cdr", columnDefinition = "MEDIUMTEXT")
//     private String jsonCdr;
// }
