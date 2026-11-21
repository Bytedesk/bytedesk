package com.bytedesk.disk.permission;

import com.bytedesk.core.base.BaseRequest;

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
public class DiskPermissionRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String spaceUid;

    private String folderUid;

    /** MEMBER / DEPT */
    private String targetType;

    private String targetUid;

    private String targetName;

    /** 逗号分隔：VIEW,DOWNLOAD,UPLOAD,EDIT,MANAGE */
    private String perms;
}
