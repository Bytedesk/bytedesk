/*
 * @Author: jackning 270580156@qq.com
 * @Description: Customer company industry enum (store as string code)
 */
package com.bytedesk.crm.customer_company;

public enum CustomerCompanyIndustryEnum {

    SAAS("saas", "SaaS"),
    ECOMMERCE("ecommerce", "电商"),
    EDUCATION("education", "教育"),
    FINANCE("finance", "金融"),
    MEDICAL("medical", "医疗"),
    MANUFACTURING("manufacturing", "制造"),
    GOVERNMENT("government", "政务"),
    OTHER("other", "其他");

    private final String code;
    private final String name;

    CustomerCompanyIndustryEnum(String code, String name) {
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
