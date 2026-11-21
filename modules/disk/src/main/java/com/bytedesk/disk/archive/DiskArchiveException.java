package com.bytedesk.disk.archive;

import lombok.Getter;

@Getter
public class DiskArchiveException extends RuntimeException {
    private final int status;

    public DiskArchiveException(int status, String message) {
        super(message);
        this.status = status;
    }

    public static DiskArchiveException unavailable() {
        return new DiskArchiveException(404, "文件不存在或不可访问");
    }
}
