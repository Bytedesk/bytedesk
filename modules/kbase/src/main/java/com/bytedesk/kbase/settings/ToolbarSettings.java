/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Toolbar visibility switches for chat UI toolbar
 */
package com.bytedesk.kbase.settings;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
