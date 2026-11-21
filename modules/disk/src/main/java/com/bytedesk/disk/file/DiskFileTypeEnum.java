package com.bytedesk.disk.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 文件类型分类：目录列表/搜索按类型一键过滤（图片/音频/视频/pdf/doc 等）
 *
 * 兼容三种传值：分类编码（如 IMAGE）映射为该分类全部扩展名；
 * 单个扩展名（如 pdf）；逗号分隔扩展名（如 jpg,png）。均按小写匹配。
 */
public enum DiskFileTypeEnum {

    IMAGE("IMAGE", List.of("jpg", "jpeg", "png", "gif", "webp", "svg", "bmp", "ico", "heic", "tiff")),
    VIDEO("VIDEO", List.of("mp4", "avi", "mkv", "mov", "webm", "flv", "wmv", "m4v", "mpg", "mpeg", "rmvb", "3gp")),
    AUDIO("AUDIO", List.of("mp3", "wav", "flac", "aac", "ogg", "wma", "m4a", "amr")),
    PDF("PDF", List.of("pdf")),
    DOC("DOC", List.of("doc", "docx", "rtf", "odt")),
    SHEET("SHEET", List.of("xls", "xlsx", "csv", "ods")),
    SLIDE("SLIDE", List.of("ppt", "pptx", "odp")),
    ARCHIVE("ARCHIVE", List.of("zip", "rar", "7z", "tar", "gz", "bz2", "xz"));

    private final String code;

    private final List<String> extensions;

    DiskFileTypeEnum(String code, List<String> extensions) {
        this.code = code;
        this.extensions = extensions;
    }

    public String getCode() {
        return code;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    /**
     * 解析过滤值为扩展名列表：空白返回空列表（不过滤）；
     * 命中分类编码返回该分类扩展名；其余按逗号分隔扩展名（小写、去空项）
     */
    public static List<String> resolveExtensions(String fileType) {
        if (fileType == null || fileType.isBlank()) {
            return List.of();
        }
        String value = fileType.trim();
        for (DiskFileTypeEnum type : values()) {
            if (type.code.equalsIgnoreCase(value)) {
                return type.extensions;
            }
        }
        List<String> extensions = new ArrayList<>();
        for (String raw : value.split(",")) {
            String extension = raw.trim().toLowerCase(Locale.ROOT);
            if (!extension.isEmpty()) {
                extensions.add(extension);
            }
        }
        return extensions;
    }
}
