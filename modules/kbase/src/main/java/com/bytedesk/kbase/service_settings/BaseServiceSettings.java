/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-14 10:45:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-20 16:53:39
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
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Embeddable
public class BaseServiceSettings implements Serializable {

    @NotBlank
    private String language = LanguageEnum.ZH_CN.name();

    @NotBlank
    private boolean autoPop = false;

    // 顶部提示开关
    @NotBlank
    private boolean showTopTip = false;
    
    @NotBlank
    private String topTip = I18Consts.I18N_TOP_TIP;

    private LocalDateTime topTipStart;

    private LocalDateTime topTipEnd;

    // 满意度评价设置
    // show rate btn on chat toolbar
    @NotBlank
    private boolean showRateBtn = false;
    
    // TODO: 自定义评价最低消息数量，未达到最低对话消息数，禁止评价
    @NotBlank
    private int rateMsgCount = 3;

    // 询前表单
    // 是否显示询前表单
    // TODO: 自定义询前表单字段
    @NotBlank
    private boolean showPreForm = false;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String preForm = BytedeskConsts.EMPTY_JSON_STRING;

    // show history message or not
    @NotBlank
    private boolean showHistory = false;

    // 输入联想开关
    @NotBlank
    private boolean showInputAssociation = false;

    // 防骚扰验证开关，TODO: 自定义验证规则: 1. 访问频率 2. 发消息时间间隔
    @NotBlank
    private boolean showCaptcha = false;

    @NotBlank
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String welcomeTip = I18Consts.I18N_WELCOME_TIP;

    @NotBlank
    private String queueTip = I18Consts.I18N_QUEUE_TIP;

    @NotBlank
    private String leavemsgTip = I18Consts.I18N_LEAVEMSG_TIP;

    /** auto close time in minutes */
    @NotBlank
    private Double autoCloseMin = Double.valueOf(25);

    // TODO: 一条消息最大长度，超过此长度，则自动截断成多条消息发送
    // @Builder.Default
    // private int msgMaxLength = 1024;
    // 
    // 常见问题
    private boolean showFaqs = false;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<FaqEntity> faqs = new ArrayList<>();
    // 快捷按钮
    private boolean showQuickFaqs = false;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<FaqEntity> quickFaqs = new ArrayList<>();
    // 猜你想问
    private boolean showGuessFaqs = false;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<FaqEntity> guessFaqs = new ArrayList<>();
    // 热门问题
    private boolean showHotFaqs = false;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<FaqEntity> hotFaqs = new ArrayList<>();
    // 快捷功能
    private boolean showShortcutFaqs = false;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<FaqEntity> shortcutFaqs = new ArrayList<>();

    /**
     * 是否显示logo
     */
    @NotBlank
    private boolean showLogo = true;

    // validate until date, when expire the service will be disabled
    private Date validateUntil;

    // 负载相关配置
    @Column(name = "max_concurrent_threads")
    private int maxConcurrentThreads = 1000;  // 最大并发会话数

    @Column(name = "max_waiting_threads")
    private int maxWaitingThreads = 100;  // 最大等待会话数

    @Column(name = "max_thread_per_agent")
    private int maxThreadPerAgent = 10;  // 每个客服最大会话数

    @Column(name = "max_waiting_time")
    private int maxWaitingTime = 300;  // 最大等待时间(秒)

    @Column(name = "alert_threshold")
    private double alertThreshold = 0.8;  // 负载告警阈值(0-1)

    // 统计数据
    @Column(name = "current_thread_count")
    private int currentThreadCount = 0;  // 当前会话数

    @Column(name = "waiting_thread_count")
    private int waitingThreadCount = 0;  // 等待会话数

    @Column(name = "online_agent_count")
    private int onlineAgentCount = 0;  // 在线客服数
}
