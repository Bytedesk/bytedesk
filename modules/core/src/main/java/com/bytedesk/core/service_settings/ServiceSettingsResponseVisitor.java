/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-14 10:45:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-02 19:28:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.service_settings;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.faq.FaqResponseVisitor;
import com.bytedesk.core.quick_button.QuickButtonResponseVisitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSettingsResponseVisitor {

    @Builder.Default
    private LanguageEnum language = LanguageEnum.ZH_CN;

    @Builder.Default
    private Boolean autoPop = false;

    /**
     * TODO: set different tips for different lang
     */
    @Builder.Default
    private Boolean showTopTip = false;

    @Builder.Default
    private String topTip = I18Consts.I18N_TOP_TIP;

    // show rate btn on chat toolbar
    @Builder.Default
    private boolean showRateBtn = false;

    // 存储到thread_log方便定时关闭会话
    /** auto close time in min - 默认自动关闭时间，单位分钟 */
    @Builder.Default
    private Double autoCloseMin = Double.valueOf(25);

    @Builder.Default
    private List<QuickButtonResponseVisitor> quickButtons = new ArrayList<>();

    @Builder.Default
    private List<FaqResponseVisitor> faqs = new ArrayList<>();

    @Builder.Default
    private Boolean showLogo = true;

}
