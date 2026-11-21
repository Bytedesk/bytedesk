package com.bytedesk.disk.archive;

import java.util.Set;

public final class DiskArchivePermissions {
    public static final String PREFIX = "DISK_ARCHIVE_";
    public static final Set<String> ALL = Set.of(
            "CONTRACT", "INVOICE").stream()
            .flatMap(module -> Set.of("READ", "UPLOAD", "DELETE", "MANAGE").stream()
                    .map(action -> PREFIX + module + "_" + action))
            .collect(java.util.stream.Collectors.toUnmodifiableSet());

    private DiskArchivePermissions() {}

    public static String read(String module) { return PREFIX + module + "_READ"; }

    public static String upload(String module) { return PREFIX + module + "_UPLOAD"; }

    public static String delete(String module) { return PREFIX + module + "_DELETE"; }

    public static String manage(String module) { return PREFIX + module + "_MANAGE"; }
}
