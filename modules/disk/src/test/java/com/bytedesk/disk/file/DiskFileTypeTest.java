package com.bytedesk.disk.file;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 文件类型分类解析纯逻辑单元测试（无 Spring 上下文）
 */
class DiskFileTypeTest {

    @Test
    void resolve_categoryCodeToExtensions() {
        assertThat(DiskFileTypeEnum.resolveExtensions("IMAGE"))
                .containsExactlyInAnyOrder("jpg", "jpeg", "png", "gif", "webp", "svg", "bmp", "ico", "heic", "tiff");
        assertThat(DiskFileTypeEnum.resolveExtensions("VIDEO")).contains("mp4", "mkv", "mov");
        assertThat(DiskFileTypeEnum.resolveExtensions("AUDIO")).contains("mp3", "wav", "flac");
        assertThat(DiskFileTypeEnum.resolveExtensions("pdf")).containsExactly("pdf");
    }

    @Test
    void resolve_categoryCodeCaseInsensitive() {
        assertThat(DiskFileTypeEnum.resolveExtensions("image")).isEqualTo(DiskFileTypeEnum.resolveExtensions("IMAGE"));
        assertThat(DiskFileTypeEnum.resolveExtensions(" Pdf ")).containsExactly("pdf");
    }

    @Test
    void resolve_plainAndCommaSeparatedExtensions() {
        assertThat(DiskFileTypeEnum.resolveExtensions("jpg")).containsExactly("jpg");
        assertThat(DiskFileTypeEnum.resolveExtensions("JPG,PNG")).containsExactly("jpg", "png");
        assertThat(DiskFileTypeEnum.resolveExtensions(" mp3 , wav ,, ")).containsExactly("mp3", "wav");
    }

    @Test
    void resolve_blankOrNullResultIsEmpty() {
        assertThat(DiskFileTypeEnum.resolveExtensions(null)).isEmpty();
        assertThat(DiskFileTypeEnum.resolveExtensions("")).isEmpty();
        assertThat(DiskFileTypeEnum.resolveExtensions("   ")).isEmpty();
    }

    @Test
    void resolve_unknownValueTreatedAsExtension() {
        // 未知编码不报错，按扩展名处理（查无结果）
        assertThat(DiskFileTypeEnum.resolveExtensions("xyz")).containsExactly("xyz");
    }
}
