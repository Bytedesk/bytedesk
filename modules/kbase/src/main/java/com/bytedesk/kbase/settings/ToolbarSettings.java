/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Toolbar visibility switches for chat UI toolbar
 */
package com.bytedesk.kbase.settings;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import lombok.AllArgsConstructor;
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
    private Boolean smile = true;

    @Builder.Default
    private Boolean image = true;

    @Builder.Default
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
    private List<String> order = Arrays.asList(
        "smile", "image", "file", "rate", "leavemsg",
        "orderSelector", "ticket", "audio", "video", "tel"
    );
}
