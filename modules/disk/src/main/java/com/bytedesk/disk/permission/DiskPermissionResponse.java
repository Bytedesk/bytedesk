package com.bytedesk.disk.permission;

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
public class DiskPermissionResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String spaceUid;

    private String folderUid;

    private String targetType;

    private String targetUid;

    private String targetName;

    private String perms;
}
