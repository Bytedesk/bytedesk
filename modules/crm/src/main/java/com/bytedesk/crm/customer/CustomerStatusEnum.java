/*
 * @Author: jackning 270580156@qq.com
 * @Description: Customer status enum
 */
package com.bytedesk.crm.customer;

public enum CustomerStatusEnum {

    NEW("new", "新建"),
    ACTIVE("active", "活跃"),
    INACTIVE("inactive", "沉默");

    private final String code;
    private final String name;

    CustomerStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
