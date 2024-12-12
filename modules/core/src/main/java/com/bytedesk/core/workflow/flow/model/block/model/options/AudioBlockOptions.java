/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-10 11:59:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-10 18:16:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.flow.model.block.model.options;

import com.bytedesk.core.workflow.flow.model.block.model.BlockOptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AudioBlockOptions extends BlockOptions {
    private String url; // 音频URL
    private String content; // Base64编码的音频内容
    private String sourceType; // URL, UPLOAD, TEXT_TO_SPEECH
    private String mimeType; // 音频类型
    private boolean autoplay; // 是否自动播放
    private String variableName; // 存储播放状态的变量名

    // 文字转语音相关配置
    private TextToSpeechConfig ttsConfig;
}
