package com.bytedesk.disk.file;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class DiskFileResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String name;

    private String fileType;

    /** FOLDER / FILE */
    private String type;

    private String spaceUid;

    private String parentUid;

    private Long size;

    /** 子节点数量（仅目录列表时返回，用于前端目录树展开标记） */
    private Long childCount;

    /** 上传人快照 JSON */
    private String uploader;

    /** 软删除时间（回收站展示用） */
    private String deletedAt;
}
