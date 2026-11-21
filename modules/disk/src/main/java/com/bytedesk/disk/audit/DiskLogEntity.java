package com.bytedesk.disk.audit;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 网盘关键操作留痕：一期记录授权变更与彻底删除
 *
 * Database Table: bytedesk_disk_log
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_disk_log", indexes = {
        @Index(name = "idx_disk_log_org_time", columnList = "org_uid, created_at"),
        @Index(name = "idx_disk_log_node", columnList = "node_uid")
})
public class DiskLogEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "space_uid")
    private String spaceUid;

    @Column(name = "node_uid")
    private String nodeUid;

    @Column(name = "operator_uid")
    private String operatorUid;

    /** 动作：PERM_CREATE/PERM_UPDATE/PERM_DELETE/DELETE_PERMANENT... */
    @Column(nullable = false, length = 64)
    private String action;

    /** 结果：SUCCESS/FAILURE */
    @Column(length = 32)
    private String result;

    /** 详情 JSON（不含文件内容、提取码或签名 URL） */
    @Column(columnDefinition = "TEXT")
    private String detail;
}
