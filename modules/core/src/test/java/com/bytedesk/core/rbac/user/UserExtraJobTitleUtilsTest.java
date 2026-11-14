package com.bytedesk.core.rbac.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserExtraJobTitleUtilsTest {

    @Test
    void getJobTitleShouldReturnEmptyForNullOrBlankExtra() {
        assertThat(UserExtraJobTitleUtils.getJobTitle(null)).isEmpty();
        assertThat(UserExtraJobTitleUtils.getJobTitle("")).isEmpty();
        assertThat(UserExtraJobTitleUtils.getJobTitle("   ")).isEmpty();
    }

    @Test
    void getJobTitleShouldReturnEmptyForInvalidJson() {
        assertThat(UserExtraJobTitleUtils.getJobTitle("not-json")).isEmpty();
        assertThat(UserExtraJobTitleUtils.getJobTitle("[1,2]")).isEmpty();
        assertThat(UserExtraJobTitleUtils.getJobTitle("\"plain\"")).isEmpty();
    }

    @Test
    void getJobTitleShouldReturnEmptyWhenKeyMissingOrNotString() {
        assertThat(UserExtraJobTitleUtils.getJobTitle("{}")).isEmpty();
        assertThat(UserExtraJobTitleUtils.getJobTitle("{\"other\":1}")).isEmpty();
        assertThat(UserExtraJobTitleUtils.getJobTitle("{\"jobTitle\":123}")).isEmpty();
        assertThat(UserExtraJobTitleUtils.getJobTitle("{\"jobTitle\":null}")).isEmpty();
    }

    @Test
    void getJobTitleShouldReturnValueWhenPresent() {
        assertThat(UserExtraJobTitleUtils.getJobTitle("{\"jobTitle\":\"经理\"}")).isEqualTo("经理");
        // 含未知键
        assertThat(UserExtraJobTitleUtils.getJobTitle("{\"foo\":\"bar\",\"jobTitle\":\"CTO\"}")).isEqualTo("CTO");
        // 转义引号等特殊字符
        assertThat(UserExtraJobTitleUtils.getJobTitle("{\"jobTitle\":\"a\\\"b\"}")).isEqualTo("a\"b");
    }

    @Test
    void putJobTitleShouldPreserveUnknownKeys() {
        String updated = UserExtraJobTitleUtils.putJobTitle("{\"foo\":\"bar\",\"n\":1}", "总监");
        assertThat(updated).contains("\"foo\":\"bar\"").contains("\"n\":1").contains("\"jobTitle\":\"总监\"");
        // 往返
        assertThat(UserExtraJobTitleUtils.getJobTitle(updated)).isEqualTo("总监");
    }

    @Test
    void putJobTitleShouldOverwriteExistingValue() {
        String updated = UserExtraJobTitleUtils.putJobTitle("{\"jobTitle\":\"old\"}", "new");
        assertThat(updated).doesNotContain("\"old\"").contains("\"jobTitle\":\"new\"");
        assertThat(UserExtraJobTitleUtils.getJobTitle(updated)).isEqualTo("new");
    }

    @Test
    void putJobTitleShouldSkipBlankValueButKeepOtherKeys() {
        String updated = UserExtraJobTitleUtils.putJobTitle("{\"jobTitle\":\"old\",\"foo\":1}", "  ");
        assertThat(updated).contains("\"jobTitle\":\"old\"").contains("\"foo\":1");
    }

    @Test
    void putJobTitleShouldCreateObjectWhenExtraInvalid() {
        String updated = UserExtraJobTitleUtils.putJobTitle("not-json", "经理");
        assertThat(UserExtraJobTitleUtils.getJobTitle(updated)).isEqualTo("经理");
        assertThat(updated).doesNotContain("not-json");
    }

    @Test
    void putJobTitleShouldCreateObjectWhenExtraNull() {
        String updated = UserExtraJobTitleUtils.putJobTitle(null, "经理");
        assertThat(updated).isEqualTo("{\"jobTitle\":\"经理\"}");
    }

    @Test
    void removeJobTitleShouldDropKeyButKeepOthers() {
        String updated = UserExtraJobTitleUtils.removeJobTitle("{\"jobTitle\":\"x\",\"foo\":\"bar\"}");
        assertThat(updated).isEqualTo("{\"foo\":\"bar\"}");
    }

    @Test
    void removeJobTitleShouldReturnEmptyObjectForInvalidExtra() {
        assertThat(UserExtraJobTitleUtils.removeJobTitle(null)).isEqualTo("{}");
        assertThat(UserExtraJobTitleUtils.removeJobTitle("bad")).isEqualTo("{}");
    }

    @Test
    void roundTripShouldSurviveSpecialCharacters() {
        String title = "高级\"工程师\" \\ / 换行\n制表\t";
        String updated = UserExtraJobTitleUtils.putJobTitle("{}", title);
        assertThat(UserExtraJobTitleUtils.getJobTitle(updated)).isEqualTo(title);
    }
}
