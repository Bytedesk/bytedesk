/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-26 12:20:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-05 22:01:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

/**
 * 角色类型常量
 *
 * @author bytedesk.com
 */
public class RoleConsts {

    private RoleConsts() {}

    // super - 超级管理员
    public static final String ROLE_SUPER = "ROLE_SUPER";
    // admin - 管理员
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    // member - 团队成员
    public static final String ROLE_MEMBER = "ROLE_MEMBER";
    // customer service - 客服
    public static final String ROLE_CUSTOMER_SERVICE = "ROLE_CS";


    /**
     * 付费注册用户
     */
    // public static final String ROLE_VIP = "ROLE_VIP";
    /**
     * 试用会员
     */
    // public static final String ROLE_TRY = "ROLE_TRY";
    /**
     * 第三方代理公司
     */
    // public static final String ROLE_PROXY = "ROLE_PROXY";
    /**
     * 免费注册用户
     */
    // public static final String ROLE_FREE = "ROLE_FREE";
    /**
     * 普通员工：非客服人员
     */
    // public static final String ROLE_MEMBER = "ROLE_MEMBER";
    /**
     * 智能客服：客服机器人
     */
    // public static final String ROLE_ROBOT = "ROLE_ROBOT";
    /**
     * 访客
     */
    // public static final String ROLE_VISITOR = "ROLE_VISITOR";
    /**
     * IM注册用户
     */
    // public static final String ROLE_USER = "ROLE_USER";

    /**
     * 工作组：
     * 客服组长
     */
    // public static final String ROLE_WORKGROUP_ADMIN = "ROLE_WORKGROUP_ADMIN";
    // /**
    //  * 客服账号：
    //  * 接待访客角色，如果要接待访客，必须赋予此角色
    //  */
    // public static final String ROLE_WORKGROUP_AGENT = "ROLE_WORKGROUP_AGENT";
    // /**
    //  * 质检账号
    //  */
    // public static final String ROLE_WORKGROUP_CHECKER = "ROLE_WORKGROUP_CHECKER";

    // /**
    //  * 良师宝用户+管理员
    //  */
    // public static final String ROLE_LIANGSHIBAO_USER = "ROLE_LIANGSHIBAO_USER";
    // public static final String ROLE_LIANGSHIBAO_ADMIN = "ROLE_LIANGSHIBAO_ADMIN";
    /**
     * 剪贴助手-微同步
     */
    // public static final String ROLE_WEITONGBU_USER = "ROLE_WEITONGBU_USER";
    /**
     * 微语AI
     */
    // public static final String ROLE_WEIYU_AI_USER = "ROLE_WEIYU_AI_USER";
    /**
     * 微语TTS-文字转语音
     */
    // public static final String ROLE_WEIYU_TTS_USER = "ROLE_WEIYU_TTS_USER";
    /**
     * 招投标
     */
    // public static final String ROLE_ZHAOTOUBIAO_USER = "ROLE_ZHAOTOUBIAO_USER";

}
