package com.bytedesk.disk.permission;

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
 * 网盘目录授权：folderUid + targetType(MEMBER/DEPT) + targetUid + perms(逗号串)
 *
 * Database Table: bytedesk_disk_permission
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_disk_permission", indexes = {
        @Index(name = "idx_disk_perm_folder", columnList = "folder_uid"),
        @Index(name = "idx_disk_perm_target", columnList = "target_type, target_uid")
})
public class DiskPermissionEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "space_uid", nullable = false)
    private String spaceUid;

    @Column(name = "folder_uid", nullable = false)
    private String folderUid;

    /** MEMBER / DEPT */
    @Column(name = "target_type", nullable = false, length = 16)
    private String targetType;

    @Column(name = "target_uid", nullable = false)
    private String targetUid;

    /** 展示名（成员昵称/部门名快照） */
    @Column(name = "target_name")
    private String targetName;

    /** 权限集合，逗号分隔：VIEW,DOWNLOAD,UPLOAD,EDIT,MANAGE */
    @Column(nullable = false)
    private String perms;

    public boolean hasPerm(DiskPermEnum perm) {
        return perms != null && perms.contains(perm.name());
    }
}
