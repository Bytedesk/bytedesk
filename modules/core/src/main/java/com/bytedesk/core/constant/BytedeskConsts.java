/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-02 21:48:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-28 20:59:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
    // 默认组织uid
    public static final String DEFAULT_ORGANIZATION_UID = "df_org_uid";
    public static final String DEFAULT_AGENT_UID = "df_ag_uid";
    public static final String DEFAULT_WORKGROUP_UID = "df_wg_uid";
    public static final String DEFAULT_ROBOT_UID = "df_rt_uid";
    public static final String DEFAULT_FILE_ASSISTANT_UID = "df_fa_uid";
    public static final String DEFAULT_CHANNEL_UID = "df_ch_uid";
    public static final String DEFAULT_AGENT_ASSISTANT_UID = "df_as_uid";
    public static final String DEFAULT_SYSTEM_UID = "df_sys_uid";
    public static final String DEFAULT_KB_QUICKREPLY_UID = "df_kb_uid";
    public static final String DEFAULT_KB_HELPCENTER_UID = "df_hc_uid";
    public static final String DEFAULT_DY_UID = "df_dy_uid";
    // 
    public static final String ACTION_LOGIN_USERNAME = "loginWithUsernamePassword";
    public static final String ACTION_LOGIN_MOBILE = "loginWithMobileCode";
    public static final String ACTION_LOGIN_EMAIL = "loginWithEmailCode";
    public static final String ACTION_LOGIN_SCAN = "loginWithScanCode";


}
