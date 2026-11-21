package com.bytedesk.disk.file;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.disk.constant.DiskConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

/**
 * 网盘文件/文件夹统一实体（type=FOLDER/FILE + parentUid）
 *
 * Database Table: bytedesk_disk_file
 * active_key：同级名称唯一键（orgUid|spaceUid|parentUid|规范化名称），
 * 软删除时置 NULL 释放名称，单列唯一索引允许多个 NULL。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_disk_file", indexes = {
        @Index(name = "uk_disk_file_active_key", columnList = "active_key", unique = true),
        @Index(name = "idx_disk_file_space_parent", columnList = "space_uid, parent_uid, is_deleted"),
        @Index(name = "idx_disk_file_org_deleted", columnList = "org_uid, is_deleted")
})
public class DiskFileEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 文件/文件夹名称（原始名称，用于展示） */
    @Column(nullable = false, length = DiskConsts.MAX_NAME_LENGTH)
    private String name;

    /** 扩展名/MIME（文件夹为空） */
    @Column(name = "file_type", length = 64)
    private String fileType;

    /** 内容 MD5（二期秒传定位，仅文件） */
    @Column(name = "content_md5", length = 64)
    private String contentMd5;

    /** 最近访问时间（预览/下载时刷新） */
    @Column(name = "last_access_at")
    private java.time.ZonedDateTime lastAccessAt;

    /** 节点类型：FOLDER / FILE */
    @Column(name = "node_type", nullable = false, length = 16)
    private String type;

    /** 所属空间 uid */
    @Column(name = "space_uid", nullable = false)
    private String spaceUid;

    /** 父目录 uid；仅空间根为空 */
    @Column(name = "parent_uid")
    private String parentUid;

    /** 同级名称唯一键；软删除时置 NULL 释放名称 */
    @Column(name = "active_key", length = 600)
    private String activeKey;

    /** 文件字节数；目录填 0 */
    @Column(name = "file_size")
    private Long size;

    /** 存储后端标识：UPLOAD（经 core upload 存储） */
    @Column(name = "storage_backend", length = 32)
    private String storageBackend;

    /** 私有存储定位符（fileUrl），不下发客户端；目录为空 */
    @Column(name = "storage_key", length = 512)
    private String storageKey;

    /** 删除前父目录（回收站恢复用） */
    @Column(name = "origin_parent_uid")
    private String originParentUid;

    /** 回收站：软删除时间 */
    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;

    /** 回收站：删除操作人 */
    @Column(name = "deleted_by")
    private String deletedBy;

    /** 扩展信息 JSON */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    /** 上传人快照 JSON（uid/nickname/avatar） */
    @Builder.Default
    @Column(name = "uploader", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String uploader = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 维护 activeKey：插入/更新时若为空则按 orgUid|spaceUid|parentUid|name 计算。
     * 根目录 parentUid 为空用常量占位，保证唯一键非 NULL。
     */
    @PrePersist
    @PreUpdate
    public void maintainActiveKey() {
        if (Boolean.TRUE.equals(isDeleted()) || type == null || name == null || getOrgUid() == null) {
            this.activeKey = null;
            return;
        }
        String parent = parentUid == null ? "-" : parentUid;
        String normalized = name.trim().toLowerCase();
        this.activeKey = String.join("|", getOrgUid(), spaceUid, parent, normalized);
    }
}
