/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-11 10:22:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
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
    // public static final String TYPE_MOBILE = "mobile";
    // public static final String TYPE_EMAIL = "email";
    // public static final String TYPE_SCAN = "scan";

    // 
    // public static final String SUPER = "SUPER";
    // public static final String ADMIN = "ADMIN";
    // public static final String MEMBER = "MEMBER";
    // public static final String CUSTOMER_SERVICE = "CS";
    // 
    // public static final String HR = "HR";
    // public static final String ORG = "ORG";
    // public static final String IT = "IT";
    // public static final String MONEY = "MONEY";
    // public static final String MARKETING = "MARKETING";
    // public static final String SALES = "SALES";

    // ROLES - 角色
    // public static final String ROLE_PREFIX = "ROLE_";
    // // super - 超级管理员
    // public static final String ROLE_SUPER = ROLE_PREFIX + "SUPER";
    // // admin - 管理员
    // public static final String ROLE_ADMIN = ROLE_PREFIX + "ADMIN";
    // // member - 成员
    // public static final String ROLE_MEMBER = ROLE_PREFIX + "MEMBER";
    // // customer service - 客服
    // public static final String ROLE_CUSTOMER_SERVICE = ROLE_PREFIX + "CS";

    // // hr - 人事
    // public static final String ROLE_HR = ROLE_PREFIX + HR;
    // // org - 行政
    // public static final String ROLE_ORG = ROLE_PREFIX + ORG;
    // // it - IT
    // public static final String ROLE_IT = ROLE_PREFIX + IT;
    // // money - 财务
    // public static final String ROLE_MONEY = ROLE_PREFIX + MONEY;
    // // marketing - 市场
    // public static final String ROLE_MARKETING = ROLE_PREFIX + MARKETING;
    // // sales - 销售
    // public static final String ROLE_SALES = ROLE_PREFIX + SALES;
    

    // /// 部门
    // public static final String DEPT_PREFIX = "DEPT.";
    // // 管理部门
    // public static final String DEPT_ADMIN = DEPT_PREFIX + "ADMIN";
    // // 成员部门
    // public static final String DEPT_MEMBER = DEPT_PREFIX + "MEMBER";
    // // 客服部门
    // public static final String DEPT_CUSTOMER_SERVICE = DEPT_PREFIX + "CS";

    // hr - 人事
    // public static final String DEPT_HR = DEPT_PREFIX + HR;
    // // org - 行政
    // public static final String DEPT_ORG = DEPT_PREFIX + ORG;
    // // it - IT
    // public static final String DEPT_IT = DEPT_PREFIX + IT;
    // // money - 财务
    // public static final String DEPT_MONEY = DEPT_PREFIX + MONEY;
    // // marketing - 市场
    // public static final String DEPT_MARKETING = DEPT_PREFIX + MARKETING;
    // // sales - 销售
    // public static final String DEPT_SALES = DEPT_PREFIX + SALES;
    // customer service - 客服
    // public static final String DEPT_CUSTOMER_SERVICE = DEPT_PREFIX + CUSTOMER_SERVICE;

    //
    // public static final String SEND_MOBILE_CODE_TYPE_LOGIN = "login";
    // public static final String SEND_MOBILE_CODE_TYPE_REGISTER = "register";
    // public static final String SEND_MOBILE_CODE_TYPE_FORGET = "forget";
    // public static final String SEND_MOBILE_CODE_TYPE_VERIFY = "verify";

    /**
     * region类型, 代码长度分别为：省 2、市 4、区/县 6、镇 9
     */
    public static final String REGION_TYPE_PROVINCE = "province";
    public static final String REGION_TYPE_CITY = "city";
    public static final String REGION_TYPE_COUNTY = "county";
    public static final String REGION_TYPE_TOWN = "town";

    //
    public static final String COLUMN_TYPE_TEXT = "TEXT"; // length = 65534
    public static final String COLUMN_TYPE_JSON = "json"; // replace with jsonb?
    //
    // public static final String ACTION_TYPE_FAILED = "failed";
    // public static final String ACTION_TYPE_LOG = "log";
}
