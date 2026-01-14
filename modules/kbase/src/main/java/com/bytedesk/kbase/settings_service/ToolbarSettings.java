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

import com.fasterxml.jackson.annotation.JsonProperty;

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
    private Boolean smile = true;

    // 机器人接待时是否显示上传按钮（用于发送图片/文件）
    // - 默认显示（true）
    // - 仅在机器人接待场景生效，人工接待仍由 upload/image/file 控制
    @Builder.Default
    private Boolean uploadRobot = true;

    // 合并 image/file 为 upload
    // - 新版本统一使用 upload
    // - 兼容旧数据/旧请求：image/file 仍可写入，但不会输出到响应
    @Getter(AccessLevel.NONE)
    @Builder.Default
    private Boolean upload = null;

    @Builder.Default
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean image = true;

    @Builder.Default
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean file = true;

    @Builder.Default
    private Boolean rate = true;

    @Builder.Default
    private Boolean leavemsg = true;

    @Builder.Default
    private Boolean orderSelector = true;

    @Builder.Default
    private Boolean ticket = true;

    @Builder.Default
    private Boolean audio = true;

    @Builder.Default
    private Boolean video = true;

    @Builder.Default
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
        "smile", "upload", "rate", "leavemsg",
        "orderSelector", "ticket", "audio", "video", "tel"
    );

    public Boolean getUpload() {
        if (upload != null) {
            return upload;
        }
        if (image == null && file == null) {
            return true;
        }
        return Boolean.TRUE.equals(image) || Boolean.TRUE.equals(file);
    }

    public void setUpload(Boolean upload) {
        this.upload = upload;
        if (upload != null) {
            this.image = upload;
            this.file = upload;
        }
    }

    public void setImage(Boolean image) {
        this.image = image;
        this.upload = (Boolean.TRUE.equals(this.image) || Boolean.TRUE.equals(this.file));
    }

    public void setFile(Boolean file) {
        this.file = file;
        this.upload = (Boolean.TRUE.equals(this.image) || Boolean.TRUE.equals(this.file));
    }

    public List<String> getOrder() {
        return normalizeOrder(this.order);
    }

    public void setOrder(List<String> order) {
        this.order = normalizeOrder(order);
    }

    private List<String> normalizeOrder(List<String> raw) {
        if (raw == null || raw.isEmpty()) {
            return Arrays.asList(
                "smile", "upload", "rate", "leavemsg",
                "orderSelector", "ticket", "audio", "video", "tel"
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
            }
            if (!out.contains(key)) {
                out.add(key);
            }
        }
        return out;
    }
}
