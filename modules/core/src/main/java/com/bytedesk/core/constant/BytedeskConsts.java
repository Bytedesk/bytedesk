/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-02 21:48:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 15:08:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.constant;

/**
 *
 * @author bytedesk.com
 */
public class BytedeskConsts {

    // Prevents instantiation
    private BytedeskConsts() {
    }
    
    // bytedesk
    public static final String PLATFORM_BYTEDESK = "BYTEDESK";
    public static final String TRACE_ID = "traceId";
    // 空字符串
    public static final String EMPTY_STRING = "";
    public static final String EMPTY_JSON_STRING = "{}";
    public static final String EMPTY_ARRAY_STRING = "[]";    
    // 默认组织uid
    public static final String DEFAULT_ORGANIZATION_UID = "df_org_uid";
    public static final String DEFAULT_UNIFIED_UID = "df_un_uid";
    public static final String DEFAULT_AGENT_UID = "df_ag_uid";
    public static final String DEFAULT_WORKGROUP_UID = "df_wg_uid"; // 默认工作组uid
    public static final String DEFAULT_WORKGROUP_UID_BEFORE = "df_wg_before"; // 售前
    public static final String DEFAULT_WORKGROUP_UID_AFTER = "df_wg_after"; // 售后
    public static final String DEFAULT_DEPARTMENT_UID = "df_dp_uid";
    public static final String DEFAULT_MEMBER_UID = "df_mb_uid";
    public static final String DEFAULT_ROBOT_UID = "df_rt_uid";
    public static final String DEFAULT_FILE_ASSISTANT_UID = "df_fa_uid";
    public static final String DEFAULT_CLIPBOARD_ASSISTANT_UID = "df_ca_uid";
    public static final String DEFAULT_INTENT_CLASSIFICATION_ASSISTANT_UID = "df_ica_uid";
    public static final String DEFAULT_INTENT_REWRITE_ASSISTANT_UID = "df_ira_uid";
    public static final String DEFAULT_EMOTION_ASSISTANT_UID = "df_ea_uid";
    public static final String DEFAULT_CHANNEL_UID = "df_ch_uid";
    public static final String DEFAULT_AGENT_ASSISTANT_UID = "df_as_uid";
    public static final String DEFAULT_SYSTEM_UID = "df_sys_uid";
    public static final String DEFAULT_KB_QUICKREPLY_UID = "df_kb_qr_uid";
    public static final String DEFAULT_KB_HELPCENTER_UID = "df_kb_hc_uid";
    public static final String DEFAULT_KB_NOTEBASE_UID = "df_kb_nb_uid";
    public static final String DEFAULT_KB_TABOO_UID = "df_kb_tb_uid";
    public static final String DEFAULT_KB_LLM_UID = "df_kb_llm_uid";
    public static final String DEFAULT_KB_KEYWORD_UID = "df_kb_kw_uid";
    public static final String DEFAULT_KB_FAQ_UID = "df_kb_faq_uid";
    public static final String DEFAULT_KB_AUTOREPLY_UID = "df_kb_ar_uid";
    public static final String DEFAULT_ROLE_SUPER_UID = "df_role_super_uid";
    public static final String DEFAULT_ROLE_ADMIN_UID = "df_role_admin_uid";
    public static final String DEFAULT_ROLE_MEMBER_UID = "df_role_member_uid";
    public static final String DEFAULT_ROLE_AGENT_UID = "df_role_agent_uid";
    // 
    public static final String ACTION_LOGIN_USERNAME = "loginWithUsernamePassword";
    public static final String ACTION_LOGIN_MOBILE = "loginWithMobileCode";
    public static final String ACTION_LOGIN_EMAIL = "loginWithEmailCode";
    public static final String ACTION_LOGIN_SCAN = "loginWithScanCode";

    // 
    public static final int COLUMN_EXTRA_LENGTH = 2048;

    // 
    // public static final String STATISTIC_FILTER_TYPE_ORG = "ORG";
    // public static final String STATISTIC_FILTER_TYPE_AGENT = "AGENT";
    // public static final String STATISTIC_FILTER_TYPE_WORKGROUP = "WORKGROUP";


}
