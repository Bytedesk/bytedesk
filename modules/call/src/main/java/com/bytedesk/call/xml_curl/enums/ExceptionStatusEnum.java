package com.bytedesk.call.xml_curl.enums;

import lombok.Getter;

/**
 * @author danmo
 * @date 2024-07-10 18:58
 */
@Getter
public enum ExceptionStatusEnum {


    ERROR_COMMON(-1,""),

    ERROR_LOGIN_VERIFY(100,"验证失败"),
    ERROR_LOGIN_USER(101,"用户名或密码错误"),
    /**
     * 1000 用户异常
     */
    ERROR_USERID_NOT_NULL(1000,"用户ID不能为空"),
    ERROR_USER_NAME_EXISTENCE(1001,"用户名称已存在"),
    ERROR_CORP_USER_ID_EXISTENCE(1002,"用户企业ID已存在"),
    ERROR_USERID_NOT_EXIST(1003,"用户不存在"),

    /**
     * 1100菜单
     */
    ERROR_MENUID_NOT_NULL(1100,"菜单ID不能为空"),

    /**
     * 1200角色
     */
    ERROR_ROLEID_NOT_NULL(1200,"角色ID不能为空"),
    ;


    ExceptionStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private int code;

    private String msg;
}
