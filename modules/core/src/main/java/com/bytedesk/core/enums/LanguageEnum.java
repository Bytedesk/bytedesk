/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-07 14:19:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-13 15:03:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.enums;

public enum LanguageEnum {
    EN,       // English
    ZH_CN,    // Simplified Chinese
    ZH_TW,    // Traditional Chinese
    ES,       // Spanish
    FR,       // French
    DE,       // German
    IT,       // Italian
    JA,       // Japanese
    KO,       // Korean
    PT,       // Portuguese
    RU,       // Russian
    AR,       // Arabic
    HI,       // Hindi
    BN,       // Bengali
    PA,       // Punjabi
    VI,       // Vietnamese
    ID,       // Indonesian
    TH,       // Thai
    MS,       // Malay
    TR,       // Turkish
    FA,       // Persian
    UR,       // Urdu
    HE,       // Hebrew
    NL,       // Dutch
    SV,       // Swedish
    DA,       // Danish
    FI,       // Finnish
    NO,       // Norwegian
    EL,       // Greek
    HU,       // Hungarian
    CS,       // Czech
    RO,       // Romanian
    PL,       // Polish
    UK,       // Ukrainian
    SK,       // Slovak
    BG,       // Bulgarian
    HR,       // Croatian
    SR,       // Serbian
    SL,       // Slovenian
    LT,       // Lithuanian
    LV,       // Latvian
    ET;       // Estonian

    public static LanguageEnum fromValue(String value) {
        for (LanguageEnum type : LanguageEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        if ("zh-cn".equalsIgnoreCase(value)) {
            return ZH_CN;
        }
        if ("zh-tw".equalsIgnoreCase(value)) {
            return ZH_TW;
        }
        throw new IllegalArgumentException("No enum constant with value: " + value);
    }
}