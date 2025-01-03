/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-14 10:45:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-23 15:33:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.service_settings;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.kbase.faq.FaqEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Embeddable
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @Builder.Default
    private String language = LanguageEnum.ZH_CN.name();

    @NotBlank
    @Builder.Default
    private boolean autoPop = false;

    // 顶部提示开关
    @NotBlank
    @Builder.Default
    private boolean showTopTip = false;
    
    @NotBlank
    @Builder.Default
    private String topTip = I18Consts.I18N_TOP_TIP;

    private LocalDateTime topTipStart;

    private LocalDateTime topTipEnd;

    // 满意度评价设置
    // show rate btn on chat toolbar
    @NotBlank
    @Builder.Default
    private boolean showRateBtn = false;
    
    // TODO: 自定义评价最低消息数量，未达到最低对话消息数，禁止评价
    @NotBlank
    @Builder.Default
    private int rateMsgCount = 3;

    // 询前表单
    // 是否显示询前表单
    // TODO: 自定义询前表单字段
    @NotBlank
    @Builder.Default
    private boolean showPreForm = false;

    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String preForm = BytedeskConsts.EMPTY_JSON_STRING;

    // show history message or not
    @NotBlank
    @Builder.Default
    private boolean showHistory = false;

    // 输入联想开关
    @NotBlank
    @Builder.Default
    private boolean showInputAssociation = false;

    // 防骚扰验证开关，TODO: 自定义验证规则: 1. 访问频率 2. 发消息时间间隔
    @NotBlank
    @Builder.Default
    private boolean showCaptcha = false;

    @NotBlank
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String welcomeTip = I18Consts.I18N_WELCOME_TIP;

    // @NotBlank
    // @Builder.Default
    // private String queueTip = I18Consts.I18N_QUEUE_TIP;

    /** auto close time in minutes */
    @NotBlank
    @Builder.Default
    private Double autoCloseMin = Double.valueOf(25);

    // TODO: 一条消息最大长度，超过此长度，则自动截断成多条消息发送
    // @Builder.Default
    // private int msgMaxLength = 1024;
    // 
    // 常见问题
    @Builder.Default
    private boolean showFaqs = false;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    private List<FaqEntity> faqs = new ArrayList<>();

    // 快捷按钮
    @Builder.Default
    private boolean showQuickFaqs = false;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    private List<FaqEntity> quickFaqs = new ArrayList<>();

    // 猜你想问
    @Builder.Default
    private boolean showGuessFaqs = false;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    private List<FaqEntity> guessFaqs = new ArrayList<>();

    // 热门问题
    @Builder.Default
    private boolean showHotFaqs = false;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    private List<FaqEntity> hotFaqs = new ArrayList<>();

    // 快捷功能
    @Builder.Default
    private boolean showShortcutFaqs = false;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    private List<FaqEntity> shortcutFaqs = new ArrayList<>();

    /**
     * 是否显示logo
     */
    @NotBlank
    @Builder.Default
    private boolean showLogo = true;

    // validate until date, when expire the service will be disabled
    private Date validateUntil;

    
}
