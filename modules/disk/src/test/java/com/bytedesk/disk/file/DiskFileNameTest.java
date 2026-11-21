package com.bytedesk.disk.file;

import com.bytedesk.disk.constant.DiskConsts;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 网盘名称与 activeKey 纯逻辑单元测试（无 Spring 上下文）
 */
class DiskFileNameTest {

    private DiskFileEntity buildFile(String orgUid, String spaceUid, String parentUid, String name, String type) {
        DiskFileEntity entity = DiskFileEntity.builder().build();
        entity.setUid("test-uid");
        entity.setOrgUid(orgUid);
        entity.setSpaceUid(spaceUid);
        entity.setParentUid(parentUid);
        entity.setName(name);
        entity.setType(type);
        return entity;
    }

    @Test
    void activeKey_containsOrgSpaceParentAndNormalizedLowercaseName() {
        DiskFileEntity file = buildFile("org-1", "space-1", "folder-1", "Report.PDF", DiskConsts.NODE_TYPE_FILE);
        file.maintainActiveKey();
        assertThat(file.getActiveKey()).isEqualTo("org-1|space-1|folder-1|report.pdf");
    }

    @Test
    void activeKey_usesPlaceholderWhenRootFolder() {
        DiskFileEntity root = buildFile("org-1", "space-1", null, DiskConsts.ROOT_NAME_PERSONAL, DiskConsts.NODE_TYPE_FOLDER);
        root.maintainActiveKey();
        assertThat(root.getActiveKey()).isEqualTo("org-1|space-1|-|我的文件");
    }

    @Test
    void activeKey_clearedWhenDeleted() {
        DiskFileEntity file = buildFile("org-1", "space-1", "folder-1", "a.txt", DiskConsts.NODE_TYPE_FILE);
        file.setDeleted(true);
        file.maintainActiveKey();
        assertThat(file.getActiveKey()).isNull();
    }

    @Test
    void diskConsts_typeValues() {
        assertThat(DiskConsts.NODE_TYPE_FOLDER).isEqualTo("FOLDER");
        assertThat(DiskConsts.NODE_TYPE_FILE).isEqualTo("FILE");
        assertThat(DiskConsts.SPACE_TYPE_PERSONAL).isEqualTo("PERSONAL");
        assertThat(DiskConsts.SPACE_TYPE_SHARED).isEqualTo("SHARED");
    }

    // ==================== 目录上传文件名规范化（回归：名称包含非法字符） ====================

    @Test
    void normalizeUploadFileName_extractsBasenameFromWebKitRelativePath() {
        // Safari/WebKit webkitdirectory：File.name/multipart filename 携带相对路径
        assertThat(DiskFileRestService.normalizeUploadFileName("docs/sub/report.pdf")).isEqualTo("report.pdf");
        assertThat(DiskFileRestService.normalizeUploadFileName("docs\\sub\\report.pdf")).isEqualTo("report.pdf");
        assertThat(DiskFileRestService.normalizeUploadFileName("./a.txt")).isEqualTo("a.txt");
    }

    @Test
    void normalizeUploadFileName_keepsPlainNameAndDefendsTraversal() {
        assertThat(DiskFileRestService.normalizeUploadFileName("report.pdf")).isEqualTo("report.pdf");
        assertThat(DiskFileRestService.normalizeUploadFileName("..")).isEqualTo("_");
        assertThat(DiskFileRestService.normalizeUploadFileName("a..b.txt")).isEqualTo("a_b.txt");
        assertThat(DiskFileRestService.normalizeUploadFileName("  report.pdf  ")).isEqualTo("report.pdf");
        assertThat(DiskFileRestService.normalizeUploadFileName(null)).isNull();
        assertThat(DiskFileRestService.normalizeUploadFileName("dir/")).isEqualTo("file");
        assertThat(DiskFileRestService.normalizeUploadFileName("dir/.")).isEqualTo("file");
    }

    @Test
    void normalizeUploadFileName_resultAlwaysPassesValidateRules() {
        for (String input : new String[] { "docs/sub/report.pdf", "docs\\a b.txt", "..", "dir/.", "./x.txt" }) {
            String normalized = DiskFileRestService.normalizeUploadFileName(input);
            assertThat(normalized).doesNotContain("/", "\\", "..");
            assertThat(normalized.trim()).isNotEmpty();
        }
    }
}
