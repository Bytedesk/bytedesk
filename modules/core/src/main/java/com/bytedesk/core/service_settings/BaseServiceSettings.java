/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-14 10:45:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-04 12:42:44
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.faq.Faq;
import com.bytedesk.core.quick_button.QuickButton;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
public class BaseServiceSettings implements Serializable {

    @NotBlank
    @Enumerated(EnumType.STRING)
    // private String language = I18Consts.ZH_CN;
    private LanguageEnum language = LanguageEnum.ZH_CN;

    @NotBlank
    private boolean autoPop = false;

    /**
     * 公告栏是否显示
     */
    @NotBlank
    private boolean showTopTip = false;

    // 公告
    @NotBlank
    private String topTip = I18Consts.I18N_TOP_TIP;

    // show rate btn on chat toolbar
    @NotBlank
    private boolean showRateBtn = false;

    

    @NotBlank
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String welcomeTip = I18Consts.I18N_WELCOME_TIP;

    @NotBlank
    private String leavemsgTip = I18Consts.I18N_LEAVEMSG_TIP;

    /** auto close time in minutes */
    @NotBlank
    private Double autoCloseMin = Double.valueOf(25);

    @ManyToMany(fetch = FetchType.LAZY)
    private List<QuickButton> quickButtons = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Faq> faqs = new ArrayList<>();

    @NotBlank
    private boolean showLogo = true;

    // validate until date, when expire the service will be disabled
    private Date validateUntil;
}
