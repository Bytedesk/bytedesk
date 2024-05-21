/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-16 11:56:53
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

public class TypeConsts {

    private TypeConsts() {
    }

    public static final String TYPE_SYSTEM = "system";
    public static final String TYPE_MOBILE = "mobile";
    public static final String TYPE_EMAIL = "email";

    // ROLES - 角色
    public static final String ROLE_PREFIX = "ROLE_";

    // super - 超级管理员
    public static final String ROLE_SUPER = "ROLE_SUPER";
    public static final String AUTHORITY_SUPER = "SUPER";

    // admin - 管理员
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String AUTHORITY_ADMIN = "ADMIN";

    // hr - 人事
    public static final String ROLE_HR = "ROLE_HR";
    public static final String AUTHORITY_HR = "HR";

    // org - 行政
    public static final String ROLE_ORG = "ROLE_ORG";
    public static final String AUTHORITY_ORG = "ORG";

    // it - IT
    public static final String ROLE_IT = "ROLE_IT";
    public static final String AUTHORITY_IT = "IT";

    // money - 财务
    public static final String ROLE_MONEY = "ROLE_MONEY";
    public static final String AUTHORITY_MONEY = "MONEY";

    // marketing - 市场
    public static final String ROLE_MARKETING = "ROLE_MARKETING";
    public static final String AUTHORITY_MARKETING = "MARKETING";

    // sales - 销售
    public static final String ROLE_SALES = "ROLE_SALES";
    public static final String AUTHORITY_SALES = "SALES";

    // customer service - 客服
    public static final String ROLE_CUSTOMER_SERVICE = "ROLE_CS";
    public static final String AUTHORITY_CUSTOMER_SERVICE = "CS";

    /// 部门
    // hr - 人事
    public static final String DEPT_HR = "DEPT_HR";

    // org - 行政
    public static final String DEPT_ORG = "DEPT_ORG";

    // it - IT
    public static final String DEPT_IT = "DEPT_IT";

    // money - 财务
    public static final String DEPT_MONEY = "DEPT_MONEY";

    // marketing - 市场
    public static final String DEPT_MARKETING = "DEPT_MARKETING";

    // sales - 销售
    public static final String DEPT_SALES = "DEPT_SALES";

    // customer service - 客服
    public static final String DEPT_CUSTOMER_SERVICE = "DEPT_CS";

    //
    public static final String SEND_MOBILE_CODE_TYPE_LOGIN = "login";
    public static final String SEND_MOBILE_CODE_TYPE_REGISTER = "register";
    public static final String SEND_MOBILE_CODE_TYPE_FORGET = "forget";
    public static final String SEND_MOBILE_CODE_TYPE_VERIFY = "verify";


    /**
     * region类型, 代码长度分别为：省 2、市 4、区/县 6、镇 9
     */
    public static final String REGION_TYPE_PROVINCE = "province";
    public static final String REGION_TYPE_CITY = "city";
    public static final String REGION_TYPE_COUNTY = "county";
    public static final String REGION_TYPE_TOWN = "town";

    
}
