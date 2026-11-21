package com.bytedesk.disk.permission;

/**
 * 网盘目录权限枚举（一期 5 级，集合语义非等级语义）
 */
public enum DiskPermEnum {

    VIEW("浏览目录、预览"),
    DOWNLOAD("下载/ZIP"),
    UPLOAD("上传、新建文件夹"),
    EDIT("重命名、同空间移动"),
    MANAGE("删除、恢复、授权、彻底删除");

    private final String description;

    DiskPermEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
