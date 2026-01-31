/*
 * @Author: jackning 270580156@qq.com
 * @Description: Customer source enum
 */
package com.bytedesk.crm.customer;

public enum CustomerSourceEnum {

    WEB("web", "网站"),
    OFFLINE("offline", "线下"),
    IMPORT("import", "导入"),
    OTHER("other", "其他");

    private final String code;
    private final String name;

    CustomerSourceEnum(String code, String name) {
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
