/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-04 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import lombok.Builder;
import lombok.Data;

/**
 * 拼音多种格式结果
 */
@Data
@Builder
public class PinyinResult {

    /** 普通格式，不带声调 */
    private String normal;

    /** 带声调格式 */
    private String tone;

    /** 首字母格式 */
    private String firstLetter;

    /** 数字声调格式 */
    private String numLast;

    public static PinyinResult empty() {
        return PinyinResult.builder()
                .normal("")
                .tone("")
                .firstLetter("")
                .numLast("")
                .build();
    }
}
