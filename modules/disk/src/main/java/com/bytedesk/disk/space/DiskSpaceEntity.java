package com.bytedesk.disk.space;

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
 * 网盘空间：每组织一个共享空间 + 每成员一个个人空间
 *
 * Database Table: bytedesk_disk_space
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_disk_space", indexes = {
        @Index(name = "idx_disk_space_org_type", columnList = "org_uid, space_type"),
        @Index(name = "idx_disk_space_user", columnList = "user_uid")
})
public class DiskSpaceEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 空间名称 */
    private String name;

    /** 空间类型：SHARED / PERSONAL */
    @Column(name = "space_type", nullable = false, length = 32)
    private String type;

    /** 描述 */
    @Column(length = 512)
    private String description;

    /** 根目录 uid（初始化时创建，冗余存储便于直接定位） */
    @Column(name = "root_uid")
    private String rootUid;
}
