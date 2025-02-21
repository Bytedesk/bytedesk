/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-10 14:59:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-10 21:35:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Embeddable
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class InviteSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否显示邀请
     */
    @Builder.Default
    @Column(name = "is_invite_show")
    private Boolean show = false;
    
    /**
     * 邀请文本
     */
    @Builder.Default
    @Column(name = "invite_text")
    private String text = "您好,请问有什么可以帮您?";
    
    /**
     * 邀请图标
     */
    @Builder.Default
    @Column(name = "invite_icon")
    private String icon = "default";
    
    /**
     * 邀请延迟时间,单位:毫秒
     */
    @Builder.Default
    @Column(name = "invite_delay")
    private Long delay = 3000L;
    
    /**
     * 是否循环
     */
    @Builder.Default
    @Column(name = "is_invite_loop")
    private Boolean loop = false;
    
    /**
     * 循环延迟时间,单位:毫秒 
     */
    @Builder.Default
    @Column(name = "invite_loop_delay")
    private Long loopDelay = 60000L;
    
    /**
     * 循环次数
     */
    @Builder.Default
    @Column(name = "invite_loop_count")
    private Integer loopCount = 3;
    
}
