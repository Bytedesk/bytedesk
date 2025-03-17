/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-14 10:45:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-28 14:27:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.kbase.faq.FaqResponseVisitor;

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
public class ServiceSettingsResponseVisitor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private LanguageEnum language = LanguageEnum.ZH_CN;

    @Builder.Default
    private Boolean autoPopup = false;

    @Builder.Default
    private Boolean showTopTip = false;

    @Builder.Default
    private String topTip = I18Consts.I18N_TOP_TIP;

    @Builder.Default
    private Boolean showRateBtn = false;

    @Builder.Default
    private int rateMsgCount = 3;

    @Builder.Default
    private Boolean showPreForm = false;

    @Builder.Default
    private String preForm = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    private Boolean showHistory = false;

    // 输入联想开关
    @Builder.Default
    private Boolean showInputAssociation = false;

    @Builder.Default
    private boolean showCaptcha = false;

    /** auto close time in min - 默认自动关闭时间，单位分钟 */
    @Builder.Default
    private Double autoCloseMin = Double.valueOf(25);

    // 桌面版聊天窗口右侧iframe
    private Boolean showRightIframe;

    // 桌面版聊天窗口右侧iframe地址
    private String rightIframeUrl;

    // 是否显示预搜索
    private Boolean showPreSearch;

    // 欢迎问题
    @Builder.Default
    private List<FaqResponseVisitor> welcomeFaqs = new ArrayList<>();

    @Builder.Default
    private Boolean showFaqs = true;
    @Builder.Default
    private List<FaqResponseVisitor> faqs = new ArrayList<>();

    @Builder.Default
    private Boolean showQuickFaqs = true;
    // 快捷问题
    @Builder.Default
    private List<FaqResponseVisitor> quickFaqs = new ArrayList<>();
    //
    @Builder.Default
    private Boolean showGuessFaqs = true;
    // 猜你想问
    @Builder.Default
    private List<FaqResponseVisitor> guessFaqs = new ArrayList<>();
    //
    @Builder.Default
    private Boolean showHotFaqs = true;
    // 热门问题
    @Builder.Default
    private List<FaqResponseVisitor> hotFaqs = new ArrayList<>();
    //
    @Builder.Default
    private Boolean showShortcutFaqs = true;
    // 快捷功能
    @Builder.Default
    private List<FaqResponseVisitor> shortcutFaqs = new ArrayList<>();

    @Builder.Default
    private Boolean showLogo = true;

    // robot
    // 是否允许转人工
    // private Boolean allowTransferToAgent;

    // // 限制仅允许：workgroup、appointed
    // private ThreadTypeEnum transferType;

    // // agentUid or workgroupUid
    // private String transferToUid;

}
