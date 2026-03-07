/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Toolbar visibility switches for chat UI toolbar
 */
package com.bytedesk.kbase.settings_service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.bytedesk.core.converter.StringListConverter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToolbarSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    // 默认显示（未设置则显示）
    @Builder.Default
    @Column(name = "toolbar_smile")
    private Boolean smile = true;

    // 机器人接待时是否显示上传按钮（用于发送图片/文件）
    // - 默认显示（true）
    // - 仅在机器人接待场景生效，人工接待由 upload 控制
    @Builder.Default
    @Column(name = "toolbar_upload_robot")
    private Boolean uploadRobot = true;

    // 语音转文字输入按钮
    @Builder.Default
    @Column(name = "toolbar_speech_input")
    private Boolean speechInput = true;

    // 语音输入按钮（输入框右侧录音能力）
    @Builder.Default
    @Column(name = "toolbar_voice_input")
    private Boolean voiceInput = true;

    // 上传按钮（已统一替代 image/file）
    @Builder.Default
    @Column(name = "toolbar_upload")
    private Boolean upload = true;

    @Builder.Default
    @Column(name = "toolbar_rate")
    private Boolean rate = true;

    @Builder.Default
    @Column(name = "toolbar_leavemsg")
    private Boolean leavemsg = true;

    @Builder.Default
    @Column(name = "toolbar_goods")
    private Boolean goods = true;

    @Builder.Default
    @Column(name = "toolbar_order_selector")
    private Boolean orderSelector = true;

    @Builder.Default
    @Column(name = "toolbar_ticket")
    private Boolean ticket = true;

    @Builder.Default
    @Column(name = "toolbar_webrtc")
    private Boolean webrtc = true;

    @Builder.Default
    @Column(name = "toolbar_tel")
    private Boolean tel = true;

    // 工具栏排列顺序（从左到右），未设置则按默认顺序
    // 说明：
    // - 仅控制排序，不影响开关是否显示；是否显示由上面的 Boolean 字段决定
    // - 为兼容性与简化持久化，使用 StringListConverter 在单列中以逗号分隔方式持久化
    // - 默认顺序覆盖所有内置项，新增项请追加到末尾
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "toolbar_order", length = 1024)

    @Getter(AccessLevel.NONE)
    private List<String> order = Arrays.asList(
        "smile", "upload", "speechInput", "rate", "leavemsg", "goods",
        "orderSelector", "ticket", "webrtc", "tel"
    );

    public List<String> getOrder() {
        return normalizeOrder(this.order);
    }

    public void setOrder(List<String> order) {
        this.order = normalizeOrder(order);
    }

    private List<String> normalizeOrder(List<String> raw) {
        if (raw == null || raw.isEmpty()) {
            return Arrays.asList(
                "smile", "upload", "speechInput", "rate", "leavemsg", "goods",
                "orderSelector", "ticket", "webrtc", "tel"
            );
        }
        final List<String> out = new ArrayList<>();
        for (String k : raw) {
            if (k == null || k.isBlank()) {
                continue;
            }
            String key = k.trim();
            if ("image".equals(key) || "file".equals(key)) {
                key = "upload";
            } else if ("audio".equals(key) || "video".equals(key)) {
                key = "webrtc";
            }
            if (!out.contains(key)) {
                out.add(key);
            }
        }
        return out;
    }
}
