package com.bytedesk.disk.file;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class DiskFileRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String name;

    /** FOLDER / FILE（查询过滤用） */
    private String nodeType;

    private String spaceUid;

    private String parentUid;

    /** 移动/复制目标目录 */
    private String targetUid;

    /** 批量操作的 uid 列表 */
    private List<String> uids;

    /** 恢复时的目标目录（可选，缺省恢复回原父目录） */
    private String restoreTargetUid;

    private String keyword;

    private String fileType;

    private String sortField;
}
